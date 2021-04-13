package com.flight.availability.controller;

import javax.xml.datatype.DatatypeConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.flight.availability.parser.AvailabilityParser;
import com.flight.availability.response.AvailabilityWrapper;
import com.flight.soap.availability.Availability;

@RestController
public class AvailabilittyController {

	private static final String baseURL = "http://private-anon-36f987f0b6-mockairline.apiary-mock.com/flights/";
	
	@Autowired
	private AvailabilityParser availabilityParser;
	
	@Autowired
	RestTemplate template;


	
	@RequestMapping(value="/flights/origin/{origin}/destination/{destination}/departure/{departure}"
			+ "/return/{returnDate}/passengers/{passengers}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AvailabilityWrapper> getFlightAvailability(@PathVariable("origin") String origin,
										  	  @PathVariable("destination") String destination,
											  @PathVariable("departure") String departure,
											  @PathVariable("returnDate") String returnDate,
											  @PathVariable("passengers") Integer passengers) throws DatatypeConfigurationException{
		
		Availability av = template.getForObject(baseURL+origin+"/"+destination+"/"+departure+"/"+returnDate+"/"+passengers,
										Availability.class);

		AvailabilityWrapper wrapper = new AvailabilityWrapper(availabilityParser.parseAvailability(av));
		
		return ResponseEntity.ok(wrapper);
	}
	
}
