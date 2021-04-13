package com.flight.availability.response;

import lombok.Data;

@Data
public class FarePrices {

    private FlightClass first;
    private FlightClass business;
    private FlightClass economy;
}
