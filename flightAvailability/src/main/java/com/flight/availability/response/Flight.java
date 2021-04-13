package com.flight.availability.response;

import lombok.Data;

@Data
public class Flight {

    private String operator;
    private String flightNumber;
    private String departsFrom;
    private String arrivesAt;
    private DepartsOn departsOn;
    private ArrivesOn arrivesOn;
    private String flightTime;
    private FarePrices farePrices;
    
}
