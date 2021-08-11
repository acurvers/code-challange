package com.example.helloworld.resources;

import com.example.helloworld.core.FlightSchedule;
import com.example.helloworld.db.FlightScheduleDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.joda.time.DateTime;

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

    @Path("/search")
    @GET
    @UnitOfWork
    public List<FlightSchedule> listFlight(@QueryParam("maxStops") Optional<Integer> maxStops,
                                           @QueryParam("departureCity") Optional<String> departureCity,
                                           @QueryParam("departureAirportCode") Optional<String> departureAirportCode,
                                           @QueryParam("destinationCity") Optional<String> destinationCity,
                                           @QueryParam("destinationAirportCode") Optional<String> destinationAirportCode,
                                           @QueryParam("maxDuration") Optional<Integer> maxDuration,
                                           @QueryParam("departure") Optional<String> departure,
                                           @QueryParam("arrival") Optional<String> arrival) {
        final List<FlightSchedule> flightSchedules = flightScheduleDAO.simpleSearch(
            maxStops,
            departureCity,
            departureAirportCode,
            destinationCity,
            destinationAirportCode,
            maxDuration,
            departure.map(this::parseDateTime),
            arrival.map(this::parseDateTime));
        return flightSchedules;
    }

    private DateTime parseDateTime(String x) {
        try {
            return DateTime.parse(x);
        } catch (Exception ex){
            return null;
        }
    }
}
