package com.booking.challenge.resources;

import com.booking.challenge.util.DateUtil;
import com.booking.challenge.core.FlightSchedule;
import com.booking.challenge.db.FlightScheduleDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Response listAllFlights(@QueryParam("maxStops") Optional<Integer> maxStops,
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
            departureDate.map(DateUtil::parseDateTimeStartDay),
            arrivalDate.map(DateUtil::parseDateTimeStartDay),
            departureDateTime.map(DateUtil::parseDateTime),
            departureDateTime.map(DateUtil::parseDateTime));
//        TODO implementation of timezones
        if (flightSchedules.isEmpty()) {
            LOGGER.debug("Returning 404 not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        LOGGER.debug("GET /search Returning flight schedule count: {}", flightSchedules.size());
        return Response.ok(flightSchedules).build();
    }

    @GET
    @UnitOfWork
    public Response listAllFlights() {
        final List<FlightSchedule> flightSchedules = flightScheduleDAO.findAll();
        if (flightSchedules.isEmpty()) {
            LOGGER.debug("Returning 404 not found");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        LOGGER.debug("GET / Returning flight schedule count: {}", flightSchedules.size());
        return Response.ok(flightSchedules).build();
    }


}
