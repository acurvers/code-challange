package com.example.helloworld.core;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.StringJoiner;

@Entity
@Table(name = "flightschedule")
@NamedQueries(
    {
        @NamedQuery(
            name = FlightSchedule.FIND_ALL_QUERY_NAME,
            query = "SELECT f FROM FlightSchedule f"
        ),
        @NamedQuery(
            name = FlightSchedule.SIMPLE_SEARCH_QUERY_NAME,
            query = "SELECT f FROM FlightSchedule f "
                + "WHERE"
                + " ( :maxStops IS NULL OR f.stops < :maxStops ) "
                + "AND"
                + " ( :maxDurationMinutes IS NULL OR f.durationMinutes < :maxDurationMinutes ) "
                + "AND"
                + " ( :departureCity IS NULL OR f.departureCity = :departureCity ) "
                + "AND"
                + " ( :departureAirportCode IS NULL OR f.departureAirportCode = :departureAirportCode ) "
                + "AND"
                + " ( :destinationAirportCode IS NULL OR f.destinationAirportCode = :destinationAirportCode ) "
                + "AND"
                + " ( :destinationCity IS NULL OR f.destinationCity = :destinationCity ) "
                + "AND"
                + " ( :departureDate IS NULL OR f.departure >= :departureDate AND f.departure < :departureNextDay ) "
                + "AND"
                + " ( :arrivalDate IS NULL OR f.arrival >= :arrivalDate AND f.arrival < :arrivalNextDay ) "
                + "AND"
                + " ( :departureDateTime IS NULL OR f.departure = :departureDateTime ) "
                + "AND"
                + " ( :arrivalDateTime IS NULL OR f.arrival = :departureDateTime ) "
        )
    }
)
public class FlightSchedule {

    public static final String FIND_ALL_QUERY_NAME = "com.example.helloworld.core.FlightSchedule.findAll";
    public static final String SIMPLE_SEARCH_QUERY_NAME = "com.example.helloworld.core.FlightSchedule.simpleSearch";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "durationMinutes")
    @Min(value = 0)
    @Max(value = 9999)
    private Integer durationMinutes;

    @Column(name = "stops")
    @Min(value = 0)
    @Max(value = 9999)
    private Integer stops;

    //    price in euro cents
    @Column(name = "price")
    private Integer price;

    @Column(name = "airline")
    private String airline;

    @Column(name = "departureAirportCode")
    private String departureAirportCode;

    @Column(name = "destinationAirportCode")
    private String destinationAirportCode;

    @Column(name = "departureCity")
    private String departureCity;

    @Column(name = "destinationCity")
    private String destinationCity;

    @Column
    private DateTime departure;

    @Column
    private DateTime arrival;

    public FlightSchedule() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getStops() {
        return stops;
    }

    public void setStops(Integer stops) {
        this.stops = stops;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
    }

    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }

    public void setDestinationAirportCode(String destinationAirportCode) {
        this.destinationAirportCode = destinationAirportCode;
    }

    @Nullable
    public DateTime getDeparture() {
        return departure;
    }

    public void setDeparture(@Nullable DateTime departure) {
        this.departure = departure;
    }

    @Nullable
    public DateTime getArrival() {
        return arrival;
    }

    public void setArrival(@Nullable DateTime arrival) {
        this.arrival = arrival;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FlightSchedule that = (FlightSchedule) o;
        return id == that.id &&
            Objects.equal(durationMinutes, that.durationMinutes) &&
            Objects.equal(stops, that.stops) &&
            Objects.equal(price, that.price) &&
            Objects.equal(airline, that.airline) &&
            Objects.equal(departureAirportCode, that.departureAirportCode) &&
            Objects.equal(destinationAirportCode, that.destinationAirportCode) &&
            Objects.equal(departureCity, that.departureCity) &&
            Objects.equal(destinationCity, that.destinationCity) &&
            (departure == null || departure.compareTo(that.departure) == 0) &&
            (arrival == null || arrival.compareTo(that.arrival) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, durationMinutes, stops, price, airline, departureAirportCode, destinationAirportCode, departureCity, destinationCity,
            departure,
            arrival);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FlightSchedule.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("durationMinutes=" + durationMinutes)
            .add("stops=" + stops)
            .add("price=" + price)
            .add("airline='" + airline + "'")
            .add("departureAirportCode='" + departureAirportCode + "'")
            .add("destinationAirportCode='" + destinationAirportCode + "'")
            .add("departureCity='" + departureCity + "'")
            .add("destinationCity='" + destinationCity + "'")
            .add("departure=" + departure)
            .add("arrival=" + arrival)
            .toString();
    }
}
