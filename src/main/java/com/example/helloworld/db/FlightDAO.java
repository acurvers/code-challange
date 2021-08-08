package com.example.helloworld.db;

import com.example.helloworld.core.Flight;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class FlightDAO extends AbstractDAO<Flight> {
    public FlightDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Flight> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Flight create(Flight flight) {
        return persist(flight);
    }

    public List<Flight> findAll() {
        return list(namedTypedQuery("com.example.helloworld.core.Flight.findAll"));
    }
}
