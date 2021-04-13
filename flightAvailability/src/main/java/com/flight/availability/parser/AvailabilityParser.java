package com.flight.availability.parser;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.flight.availability.response.ArrivesOn;
import com.flight.availability.response.AvailabilityResponse;
import com.flight.availability.response.DepartsOn;
import com.flight.availability.response.FarePrices;
import com.flight.availability.response.Flight;
import com.flight.availability.response.FlightClass;
import com.flight.availability.response.FlightCurrencyAmount;
import com.flight.availability.response.FlightTime;
import com.flight.soap.availability.Availability;
import com.flight.soap.availability.Availability.Flight.Fares;
import com.flight.soap.availability.Availability.Flight.Fares.Fare;

@Component
public class AvailabilityParser {
 
	/**
	* This method is responsible for parsing the data from XML to Java Object
	*
	* @param  availability data from airline API in XML format
	* @return              parsed data from XML to Java Object
	* 
	*/
	public AvailabilityResponse parseAvailability(Availability availability) {
		AvailabilityResponse response = new AvailabilityResponse();

		List<Flight> flights = new ArrayList<Flight>();

		for (Availability.Flight f : availability.getFlight()) {
			Flight flightResponse = new Flight();

			flightResponse.setArrivesOn((ArrivesOn) getTime(f.getArrivalDate(), new ArrivesOn()));
			flightResponse.setDepartsOn((DepartsOn) getTime(f.getDepartureDate(), new DepartsOn()));
			flightResponse.setArrivesAt(f.getDestinationAirport());
			flightResponse.setDepartsFrom(f.getOriginAirport());

			flightResponse.setFarePrices(getFarePrices(f.getFares()));
			flightResponse.setFlightNumber(f.getFlightDesignator());
			flightResponse.setOperator(f.getCarrierCode());

			
			flightResponse.setFlightTime(getFlightDuration(f.getDepartureDate(), f.getArrivalDate()));
			flights.add(flightResponse);

		}
		response.setFlight(flights);
		return response;
	}

	/**
	* This method is responsible for formatting date from XMLGregorianCalendar into  
	* String format
	* 
	* @param  calendar  date in XMLGregorianCalendar format
	* @Param  ft        FlightTime object
	* @return ft        FlightTime object containing formatted Date and Time			      
	* 
	*/
	private FlightTime getTime(XMLGregorianCalendar calendar, FlightTime ft) {

		ft.setDate(getFormatedDate(calendar, "dd-MM-yyyy"));
		ft.setTime(getFormatedDate(calendar, "hh:mmaa"));

		return ft;
	}

	/**
	* This method is responsible for converting and formatting date from XMLGregorianCalendar into  
	* String format
	* 
	* @param  calendar  date in XMLGregorianCalendar format
	* @Param  format    contains the format on which the date should be formatted
	* @return 	        String object containing formatted Date or Time			      
	* 
	*/
	private String getFormatedDate(XMLGregorianCalendar calendar, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		GregorianCalendar gc = calendar.toGregorianCalendar();
		return sdf.format(gc.getTime());

	}

	/**
	* This method is responsible for parsing the flight data from XML into Java object 
	* and setting up the values to new desire style
	* 
	* @param  list  		date in XMLGregorianCalendar format
	* @return FarePrices	object containing flight tickets information			      
	* 
	*/
	private FarePrices getFarePrices(Fares list) {

		FarePrices fp = new FarePrices();
		for (Fare fare : list.getFare()) {
			FlightClass fc = new FlightClass();

			switch (fare.getClazz()) {
			case "FIF":
				fc.setBookingFee(getPrice(fare.getFees()));
				fc.setTax(getPrice(fare.getTax()));
				fc.setTicket(getPrice(fare.getBasePrice()));
				fp.setFirst(fc);

				break;

			case "CIF":
				fc.setBookingFee(getPrice(fare.getFees()));
				fc.setTax(getPrice(fare.getTax()));
				fc.setTicket(getPrice(fare.getBasePrice()));
				fp.setBusiness(fc);

				break;

			case "YIF":
				fc.setBookingFee(getPrice(fare.getFees()));
				fc.setTax(getPrice(fare.getTax()));
				fc.setTicket(getPrice(fare.getBasePrice()));
				fp.setEconomy(fc);

				break;

			default:
				break;
			}
		}

		return fp;

	}

	/**
	* This method is responsible for splitting the Currency and the Amount. 
	* and setting up the values to new desire style
	* 
	* @param  value  				contains currency and amount
	* @return FlightCurrencyAmount	object containing currency and amount for (BookingFee, Ticket, Fees)			      
	* 
	*/
	private FlightCurrencyAmount getPrice(String value) {

		String[] vet = value.split(" ");
		String currency = vet[0];
		BigDecimal amount = new BigDecimal(vet[1]);

		return new FlightCurrencyAmount(currency, amount);

	}

	/**
	* This method is responsible for extracting the Flight duration by subtracting ( arrivelTiime - departureTime ) 
	* and setting up the values to new desire style
	* 
	* @param  departureTime  				date in XMLGregorianCalendar format of the Flight's departure time
	* @param  arrivalTime  					date in XMLGregorianCalendar format of the Flight's arrivalTime time
	* @return timeDiff						String object containing Flight's duration		      
	* 
	*/
	private String getFlightDuration(XMLGregorianCalendar departureTime, XMLGregorianCalendar arrivalTime) {

		
		long deptTime = departureTime.toGregorianCalendar().getTimeInMillis();
		long arrTime = arrivalTime.toGregorianCalendar().getTimeInMillis();
		
		Long differenceInMilliSeconds = arrTime - deptTime;
		Long diffMinutes = differenceInMilliSeconds / (60 * 1000) % 60;
		Long diffHours = differenceInMilliSeconds / (60 * 60 * 1000) % 24;
  
		String timeDiff =  StringUtils.leftPad(diffHours.toString(),2,"0") +":"+diffMinutes.toString();
		return timeDiff;
	}

}
