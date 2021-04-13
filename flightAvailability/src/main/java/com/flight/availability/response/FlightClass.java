package com.flight.availability.response;

import lombok.Data;

@Data
public class FlightClass {
	
	private FlightCurrencyAmount ticket;
	private FlightCurrencyAmount bookingFee;
	private FlightCurrencyAmount tax;

}
