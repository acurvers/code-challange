package com.example.helloworld;

import com.example.helloworld.core.FlightSchedule;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.glassfish.jersey.client.ClientProperties;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

import static io.dropwizard.testing.ConfigOverride.config;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    void testWhenSearchForNonExistingExpect404() throws Exception {
        assertThatExceptionOfType(javax.ws.rs.NotFoundException.class).isThrownBy(() ->
            search(null,
                "AMS",
                "Not exists",
                null,
                "1021-09-05",
                "1021-09-05",
                150));
    }

    @Test
    void testWhenDefaultFlightScheduleQueryExpectTwoFlighSchedules() throws Exception {
        //WHEN
        final FlightSchedule expectedFlightScheduleA = new FlightSchedule();
        expectedFlightScheduleA.setId(1L);
        expectedFlightScheduleA.setAirline("Easyjet");
        expectedFlightScheduleA.setDepartureAirportCode("AMS");
        expectedFlightScheduleA.setDepartureCity("Amsterdam");
        expectedFlightScheduleA.setDestinationAirportCode("FCO");
        expectedFlightScheduleA.setStops(0);
        expectedFlightScheduleA.setPrice(342717);
        expectedFlightScheduleA.setDestinationCity("Rome");
        expectedFlightScheduleA.setDeparture(DateTime.parse("2021-09-05T07:05+02:00"));
        expectedFlightScheduleA.setArrival(DateTime.parse("2021-09-05T09:25+02:00"));
        expectedFlightScheduleA.setDurationMinutes(140);

        //THEN
        final List<FlightSchedule> flights = search(null,
            "AMS",
            "Rome",
            null,
            "2021-09-05",
            "2021-09-05",
            150);

        //VERIFY
        assertThat(flights)
            .withFailMessage("Expected to have flight schedules returned")
            .isNotEmpty();
        assertThat(flights)
            .contains(expectedFlightScheduleA)
            .isNotEmpty();
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

    private List<FlightSchedule> search(String departureCity,
                                        String departureAirportCode,
                                        String destinationCity,
                                        String destinationAirportCode,
                                        String departureDate,
                                        String arrivalDate,
                                        Integer maxDuration) {
        final WebTarget target = getWebTarget("/flightschedule/search");

        return target
            .queryParam("departureCity", departureCity)
            .queryParam("departureAirportCode", departureAirportCode)
            .queryParam("destinationCity", destinationCity)
            .queryParam("destinationAirportCode", destinationAirportCode)
            .queryParam("departureDate", departureDate)
            .queryParam("arrivalDate", arrivalDate)
            .queryParam("departureDateTime", null)
            .queryParam("arrivalDateTime", null)
            .queryParam("maxDuration", maxDuration)
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<List<FlightSchedule>>() {
            });
    }

    private WebTarget getWebTarget(String urlPath) {
        final int READ_TIMEOUT_FOR_DEBUGGING = 500000;
        return APP.client()
            .property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT_FOR_DEBUGGING)
            .target("http://localhost:" + APP.getLocalPort() + urlPath);
    }
}
