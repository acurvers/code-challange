package com.example.helloworld.db;

import com.example.helloworld.core.FlightSchedule;
import com.mysql.cj.conf.PropertyKey;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(DropwizardExtensionsSupport.class)
@DisabledForJreRange(min = JRE.JAVA_16)
public class FlightScheduleDAOIntegrationTest {
    @Container
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.24"));

    public DAOTestExtension daoTestRule = DAOTestExtension.newBuilder()
            .customizeConfiguration(c -> c.setProperty(AvailableSettings.DIALECT, MySQL57Dialect.class.getName()))
            .setDriver(MY_SQL_CONTAINER.getDriverClassName())
            .setUrl(MY_SQL_CONTAINER.getJdbcUrl())
            .setUsername(MY_SQL_CONTAINER.getUsername())
            .setPassword(MY_SQL_CONTAINER.getPassword())
            .setProperty(PropertyKey.enabledTLSProtocols.getKeyName(), "TLSv1.1,TLSv1.2,TLSv1.3")
            .addEntityClass(FlightSchedule.class)
            .build();

    private FlightScheduleDAO flightScheduleDAO;

    @BeforeEach
    void setUp() {
        flightScheduleDAO = new FlightScheduleDAO(daoTestRule.getSessionFactory());
    }

    @Test
    void createFlightSchedule() {
        final FlightSchedule flightSchedule = new FlightSchedule();
        flightSchedule.setDestinationAirportCode("FCO");
        final FlightSchedule flightScheduleToFCO = daoTestRule.inTransaction(() -> flightScheduleDAO.create(flightSchedule));
        assertThat(flightScheduleToFCO.getId()).isGreaterThan(0);
        assertThat(flightScheduleDAO.findById(flightScheduleToFCO.getId())).isEqualTo(Optional.of(flightScheduleToFCO));
    }

    @Test
    void findAll() {
        daoTestRule.inTransaction(() -> {
            flightScheduleDAO.create(new FlightSchedule());
        });

        final List<FlightSchedule> flightSchedules = flightScheduleDAO.findAll();
        assertThat(flightSchedules).extracting("destinationAirportCode").containsOnly("FCO");
    }

    @Test
    void handlesNullFullName() {
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                daoTestRule.inTransaction(() -> flightScheduleDAO.create(new FlightSchedule())));
    }
}
