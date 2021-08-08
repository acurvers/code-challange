package com.example.helloworld.core;

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
import java.util.Objects;

@Entity
@Table(name = "flightschedule")
@NamedQueries(
    {
        @NamedQuery(
            name = FlightSchedule.FIND_ALL_QUERY_NAME,
            query = "SELECT f FROM FlightSchedule f"
        )
    }
)
public class FlightSchedule {

    public static final String FIND_ALL_QUERY_NAME = "com.example.helloworld.core.FlightSchedule.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "durationMinutes")
    @Min(value = 0)
    @Max(value = 9999)
    private int durationMinutes;

    @Column(name = "stops")
    @Min(value = 0)
    @Max(value = 9999)
    private int stops;

    //    price in euro cents
    @Column(name = "price")
    private int price;

    @Column(name = "airline")
    private String airline;

    @Column(name = "departureAirportCode")
    private String departureAirportCode;

    @Column(name = "destinationAirportCode")
    private String destinationAirportCode;

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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FlightSchedule)) {
            return false;
        }

        FlightSchedule flightSchedule = (FlightSchedule) o;

        return id == flightSchedule.id &&
            durationMinutes == flightSchedule.durationMinutes &&
            Objects.equals(departureAirportCode, flightSchedule.departureAirportCode) &&
            Objects.equals(destinationAirportCode, flightSchedule.destinationAirportCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, durationMinutes, departureAirportCode, destinationAirportCode);
    }
}
