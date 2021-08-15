package com.example.helloworld.db;

import com.example.helloworld.core.FlightSchedule;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightScheduleDAO.class);

    public List<FlightSchedule> simpleSearch(
        Optional<Integer> maxStops,
        Optional<String> departureCity,
        Optional<String> departureAirportCode,
        Optional<String> destinationCity,
        Optional<String> destinationAirportCode,
        Optional<Integer> maxDur,
        Optional<DateTime> departureDate,
        Optional<DateTime> arrivalDate,
        Optional<DateTime> departureDateTime,
        Optional<DateTime> arrivalDateTime) {
        final Query<FlightSchedule> query = namedTypedQuery(FlightSchedule.SIMPLE_SEARCH_QUERY_NAME);

        query.setParameter("maxStops", maxStops.orElse(null));
        query.setParameter("departureCity", departureCity.orElse(null));
        query.setParameter("departureAirportCode", departureAirportCode.orElse(null));
        query.setParameter("destinationCity", destinationCity.orElse(null));
        query.setParameter("destinationAirportCode", destinationAirportCode.orElse(null));
        query.setParameter("maxDurationMinutes", maxDur.orElse(null));
        query.setParameter("departureDate", departureDate.orElse(null));
        query.setParameter("arrivalDate", arrivalDate.orElse(null));
        query.setParameter("departureNextDay", departureDate.map(x -> x.plusDays(1)).orElse(null));
        query.setParameter("arrivalNextDay", arrivalDate.map(x -> x.plusDays(1)).orElse(null));
        query.setParameter("departureDateTime", departureDateTime.orElse(null));
        query.setParameter("arrivalDateTime", arrivalDateTime.orElse(null));
        final List<FlightSchedule> searchResult = list(query);
        LOGGER.debug("Returning flight schedule count: {}", searchResult.size());
        return searchResult;
    }

}
