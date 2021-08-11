package com.example.helloworld.db;

import com.example.helloworld.core.FlightSchedule;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.joda.time.DateTime;

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

    public List<FlightSchedule> simpleSearch(
        Optional<Integer> maxStops,
        Optional<String> departureCity,
        Optional<String> departureAirportCode,
        Optional<String> destinationCity,
        Optional<String> destinationAirportCode,
        Optional<Integer> maxDur,
        Optional<DateTime> departure,
        Optional<DateTime> arrival) {
        final Query<FlightSchedule> query = namedTypedQuery(FlightSchedule.SIMPLE_SEARCH_QUERY_NAME);
        query.setParameter("maxStops", maxStops.orElse(null));
        query.setParameter("departureCity", departureCity.orElse(null));
        query.setParameter("departureAirportCode", departureAirportCode.orElse(null));
        query.setParameter("destinationCity", destinationCity.orElse(null));
        query.setParameter("destinationAirportCode", destinationAirportCode.orElse(null));
        query.setParameter("maxDurationMinutes", maxDur.orElse(null));
        query.setParameter("departure", departure.orElse(null));
        query.setParameter("arrival", arrival.orElse(null));
        final List<FlightSchedule> searchResult = list(query);
        return searchResult;
    }

}
