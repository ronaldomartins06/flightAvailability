package com.flight.availability.response;

import java.util.List;

import lombok.Data;

@Data
public class AvailabilityResponse {
	
	private List<Flight> flight;

}
