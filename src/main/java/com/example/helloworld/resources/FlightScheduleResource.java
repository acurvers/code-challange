package com.example.helloworld.resources;

import com.example.helloworld.core.FlightSchedule;
import com.example.helloworld.db.FlightScheduleDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/flightschedule")
@Produces(MediaType.APPLICATION_JSON)
public class FlightScheduleResource {

    private final FlightScheduleDAO flightScheduleDAO;

    public FlightScheduleResource(FlightScheduleDAO flightScheduleDAO) {
        this.flightScheduleDAO = flightScheduleDAO;
    }

    @POST
    @UnitOfWork
    public FlightSchedule createFlight(@Valid FlightSchedule flight) {
        return flightScheduleDAO.create(flight);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightScheduleResource.class);

    @Path("/search")
    @GET
    @UnitOfWork
    public List<FlightSchedule> listFlight(@QueryParam("maxStops") Optional<Integer> maxStops,
                                           @QueryParam("departureCity") Optional<String> departureCity,
                                           @QueryParam("departureAirportCode") Optional<String> departureAirportCode,
                                           @QueryParam("destinationCity") Optional<String> destinationCity,
                                           @QueryParam("destinationAirportCode") Optional<String> destinationAirportCode,
                                           @QueryParam("maxDuration") Optional<Integer> maxDuration,
                                           @QueryParam("departureDate") Optional<String> departureDate,
                                           @QueryParam("arrivalDate") Optional<String> arrivalDate,
                                           @QueryParam("departureDateTime") Optional<String> departureDateTime,
                                           @QueryParam("arrivalDateTime") Optional<String> arrivalDateTime) {
        final List<FlightSchedule> flightSchedules = flightScheduleDAO.simpleSearch(
            maxStops,
            departureCity,
            departureAirportCode,
            destinationCity,
            destinationAirportCode,
            maxDuration,
            departureDate.map(this::parseDateTimeStartDay),
            arrivalDate.map(this::parseDateTimeStartDay),
            departureDateTime.map(this::parseDateTime),
            departureDateTime.map(this::parseDateTime));
        LOGGER.debug("Returning flight schedule count: {}", flightSchedules.size());
        return flightSchedules;
    }

    private DateTime parseDateTime(String dateTimeStr) {
        try {
            return DateTime.parse(dateTimeStr);
        } catch (Exception ex){
            return null;
        }
    }

    private DateTime parseDateTimeStartDay(String dateTimeStr) {
        final DateTime dateTime = parseDateTime(dateTimeStr);
        return(dateTime==null) ? null : dateTime.withTimeAtStartOfDay();
    }
}
