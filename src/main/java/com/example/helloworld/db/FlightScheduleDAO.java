package com.example.helloworld.db;

import com.example.helloworld.core.FlightSchedule;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class FlightScheduleDAO extends AbstractDAO<FlightSchedule> {
    public FlightScheduleDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<FlightSchedule> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public FlightSchedule create(FlightSchedule flight) {
        return persist(flight);
    }

    public List<FlightSchedule> findAll() {
        return list(namedTypedQuery(FlightSchedule.FIND_ALL_QUERY_NAME));
    }
}
