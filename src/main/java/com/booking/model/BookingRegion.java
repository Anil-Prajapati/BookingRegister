package com.booking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class BookingRegion {

	private String regionCode;
	private Double sumOfWeight;
	private Long totalDocket;
	private Double freight;
	
	public BookingRegion(String regionCode, Double sumOfWeight, Long totalDocket, Double freight) {
		super();
		this.regionCode = regionCode;
		this.sumOfWeight = sumOfWeight;
		this.totalDocket = totalDocket;
		this.freight = freight;
	}
	
	
}
