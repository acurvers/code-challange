package com.example.helloworld.resources;

import com.example.helloworld.core.FlightSchedule;
import com.example.helloworld.db.FlightScheduleDAO;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    public List<FlightSchedule> listFlight() {
        return flightScheduleDAO.findAll();
    }
}
