package com.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingSummary {

	private String bkgBasis;
	private Double sumOfWeight;
	private Long totalDocket;
	private Double freight;
	
}
