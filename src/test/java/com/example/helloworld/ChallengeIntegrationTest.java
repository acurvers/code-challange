package com.example.helloworld;

import com.example.helloworld.core.FlightSchedule;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static io.dropwizard.testing.ConfigOverride.config;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
class ChallengeIntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-example.yml");

    @TempDir
    static Path tempDir;
    static Supplier<String> CURRENT_LOG = () -> tempDir.resolve("application.log").toString();
    static Supplier<String> ARCHIVED_LOG = () -> tempDir.resolve("application-%d-%i.log.gz").toString();

    static final DropwizardAppExtension<HelloWorldConfiguration> APP = new DropwizardAppExtension<>(
        HelloWorldApplication.class, CONFIG_PATH,
        config("database.url", () -> "jdbc:h2:" + tempDir.resolve("database.h2")),
        config("logging.appenders[1].currentLogFilename", CURRENT_LOG),
        config("logging.appenders[1].archivedLogFilenamePattern", ARCHIVED_LOG)
    );

    @BeforeAll
    public static void migrateDb() throws Exception {
        APP.getApplication().run("db", "migrate", CONFIG_PATH);
    }

    @Test
    void testWhenDefaultFlightScheduleQueryExpectTwoFlighSchedules() throws Exception {
        final FlightSchedule expectedFlightScheduleA = new FlightSchedule();
        expectedFlightScheduleA.setAirline("Easyjet");
        expectedFlightScheduleA.setDepartureAirportCode("AMS");
        expectedFlightScheduleA.setDestinationAirportCode("FCO");
        expectedFlightScheduleA.setStops(0);
        expectedFlightScheduleA.setDeparture(DateTime.parse("2021-09-05T07:05+02:00"));
        expectedFlightScheduleA.setArrival(DateTime.parse("2021-09-05T09:25+02:00"));
        expectedFlightScheduleA.setDurationMinutes(140);

        final FlightSchedule expectedFlightScheduleB = new FlightSchedule();
        expectedFlightScheduleB.setDurationMinutes(140);
        expectedFlightScheduleB.setAirline("Easyjet");
        expectedFlightScheduleB.setDepartureAirportCode("AMS");
        expectedFlightScheduleB.setDestinationAirportCode("FCO");
        expectedFlightScheduleB.setStops(0);
        expectedFlightScheduleB.setDeparture(DateTime.parse("2021-09-05T07:05+02:00"));
        expectedFlightScheduleB.setArrival(DateTime.parse("2021-09-05T09:25+02:00"));

        final List<FlightSchedule> flights = search(null,
            "AMS",
            "Rome",
            null,
            "2021-09-05");
        assertThat(flights).isNotEmpty();
    }

    private List<FlightSchedule> search(String departureCity,
                                        String departureAirportCode,
                                        String destinationCity,
                                        String destinationAirportCode,
                                        String departureDate) {
        final WebTarget target = getWebTarget("/flightschedule/search");

        return target
            .queryParam("departureCity", departureCity)
            .queryParam("departureAirportCode", departureAirportCode)
            .queryParam("destinationCity", destinationCity)
            .queryParam("destinationAirportCode", destinationAirportCode)
            .queryParam("departure", departureDate)
            .queryParam("arrival", "1")
            .queryParam("maxDuration", "1")
            .request()
            .get(new GenericType<List<FlightSchedule>>() {});
    }

    private WebTarget getWebTarget(String urlPath) {
        return APP.client().target("http://localhost:" + APP.getLocalPort() + urlPath);
    }

    @Test
    void testLogFileWritten() throws IOException {
        // The log file is using a size and time based policy, which used to silently
        // fail (and not write to a log file). This test ensures not only that the
        // log file exists, but also contains the log line that jetty prints on startup
        final Path logFile = Paths.get(CURRENT_LOG.get());
        assertThat(logFile).exists();
        final String logFileContent = new String(Files.readAllBytes(logFile), UTF_8);
        assertThat(logFileContent)
            .contains("0.0.0.0:" + APP.getLocalPort(), "Starting hello-world", "Started application", "Started admin")
            .doesNotContain("Exception", "ERROR", "FATAL");
    }
}
