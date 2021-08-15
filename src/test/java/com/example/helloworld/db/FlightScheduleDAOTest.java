package com.example.helloworld.db;

import com.example.helloworld.core.FlightSchedule;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
public class FlightScheduleDAOTest {

    public DAOTestExtension daoTestRule = DAOTestExtension.newBuilder()
        .addEntityClass(FlightSchedule.class)
        .build();

    private FlightScheduleDAO flightScheduleDAO;

    @BeforeEach
    void setUp() throws Exception {
        flightScheduleDAO = new FlightScheduleDAO(daoTestRule.getSessionFactory());
    }

    @Test
    void createFlightSchedule() {
        final FlightSchedule flightSchedule1 = new FlightSchedule();
        flightSchedule1.setDestinationAirportCode("FCO");
        final FlightSchedule flightSchedule = daoTestRule.inTransaction(() -> flightScheduleDAO.create(flightSchedule1));
        assertThat(flightSchedule.getId()).isGreaterThan(0);
        assertThat(flightScheduleDAO.findById(flightSchedule.getId())).isEqualTo(Optional.of(flightSchedule));
    }

    @Test
    void findAll() {
        daoTestRule.inTransaction(() -> {
            final FlightSchedule flightSchedule1 = new FlightSchedule();
            flightSchedule1.setDestinationAirportCode("FCO");
            flightScheduleDAO.create(flightSchedule1);
        });

        final List<FlightSchedule> flightSchedules = flightScheduleDAO.findAll();
        assertThat(flightSchedules).extracting("destinationAirportCode").containsOnly("FCO");
    }
}
