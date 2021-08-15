package com.booking.challenge.resources;

import com.booking.challenge.core.FlightSchedule;
import com.booking.challenge.db.FlightScheduleDAO;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FlightScheduleResource}.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class FlightScheduleResourceTest {

    private static final FlightScheduleDAO FLIGHTSCHEDULE_DAO = mock(FlightScheduleDAO.class);
    public static final ResourceExtension RESOURCES = ResourceExtension.builder()
        .addResource(new FlightScheduleResource(FLIGHTSCHEDULE_DAO))
        .build();
    private ArgumentCaptor<FlightSchedule> flightScheduleArgumentCaptor = ArgumentCaptor.forClass(FlightSchedule.class);
    private FlightSchedule flightSchedule;

    @BeforeEach
    void setUp() {
        flightSchedule = new FlightSchedule();
        flightSchedule.setAirline("Easyjet");
        flightSchedule.setStops(0);
        flightSchedule.setDepartureAirportCode("AMS");
        flightSchedule.setDestinationAirportCode("FCO");
        flightSchedule.setDeparture(DateTime.parse("2021-09-05T07:05+02:00"));
        flightSchedule.setArrival(DateTime.parse("2021-09-05T09:25+02:00"));
        flightSchedule.setDurationMinutes(140);
    }

    @AfterEach
    void tearDown() {
        reset(FLIGHTSCHEDULE_DAO);
    }

    @Test
    void createFlight() {
        //WHEN
        when(FLIGHTSCHEDULE_DAO.create(any(FlightSchedule.class))).thenReturn(flightSchedule);

        //THEN
        final Response response = RESOURCES.target("/flightschedule")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(flightSchedule, MediaType.APPLICATION_JSON_TYPE));

        //VERIFY
        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);
        verify(FLIGHTSCHEDULE_DAO).create(flightScheduleArgumentCaptor.capture());
        assertThat(flightScheduleArgumentCaptor.getValue()).isEqualTo(flightSchedule);
    }

    @Test
    void whenSearchFlightSchedulesExpectResult() throws Exception {
        //WHEN
        final List<FlightSchedule> flightSchedules = Collections.singletonList(flightSchedule);
        when(FLIGHTSCHEDULE_DAO.findAll()).thenReturn(flightSchedules);
        when(FLIGHTSCHEDULE_DAO
            .simpleSearch(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("AMS"), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()))
            .thenReturn(flightSchedules);

        //THEN
        final List<FlightSchedule> response = RESOURCES
            .target("/flightschedule/search")
            .queryParam("destinationAirportCode", "AMS")
            .request()
            .get(new GenericType<List<FlightSchedule>>() {
            });

        //VERIFY
        verify(FLIGHTSCHEDULE_DAO)
            .simpleSearch(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("AMS"), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
        assertThat(response).containsAll(flightSchedules);
    }

    @Test
    void whenListAllFlightSchedulesExpectResult() throws Exception {
        //WHEN
        final List<FlightSchedule> flightSchedules = Collections.singletonList(flightSchedule);
        when(FLIGHTSCHEDULE_DAO.findAll())
            .thenReturn(flightSchedules);

        //THEN
        final List<FlightSchedule> response = RESOURCES
            .target("/flightschedule/")
            .request()
            .get(new GenericType<List<FlightSchedule>>() {
            });

        //VERIFY
        verify(FLIGHTSCHEDULE_DAO).findAll();
        assertThat(response).containsAll(flightSchedules);
    }
}
