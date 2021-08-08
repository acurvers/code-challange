package com.example.helloworld.core;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "flight")
@NamedQueries(
        {
                @NamedQuery(
                        name = "com.example.helloworld.core.Flight.findAll",
                        query = "SELECT f FROM Flight f"
                )
        })
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
