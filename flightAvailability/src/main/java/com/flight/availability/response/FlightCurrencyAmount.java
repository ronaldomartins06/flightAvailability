package com.flight.availability.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlightCurrencyAmount {
   
	private String currency;
	private BigDecimal amount;

}
