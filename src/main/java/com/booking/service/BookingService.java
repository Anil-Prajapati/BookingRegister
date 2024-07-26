package com.booking.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.model.Booking;
import com.booking.model.BookingRegion;
import com.booking.model.BookingSummary;
import com.booking.repository.BookingRepository;

import jakarta.persistence.Tuple;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	public Iterable<Booking> getAll() {
		return bookingRepository.findAll();
	}

	public List<Booking> getCustcode(String custcode) {
		return bookingRepository.findByCustcode(custcode);
	}

	public List<BookingSummary> getBookingSummary(String regionCode, List<String> bkgBases) {
		List<Tuple> tuples = bookingRepository.findBookingSummary(regionCode, bkgBases);
		return tuples.stream()
				.map(tuple -> new BookingSummary(tuple.get("bkgBasis", String.class),
						tuple.get("sumOfWeight", Double.class), tuple.get("totalDocket", Long.class),
						tuple.get("freight", Double.class)))
				.collect(Collectors.toList());
	}

	public List<BookingSummary> getBookingSummary(String regionCode, List<String> bkgBases, LocalDate fromDate,
			LocalDate toDate) {
		List<Tuple> tuples = bookingRepository.findBookingSummary(regionCode, bkgBases, java.sql.Date.valueOf(fromDate),
				java.sql.Date.valueOf(toDate));
		return tuples.stream()
				.map(tuple -> new BookingSummary(tuple.get("bkgBasis", String.class),
						tuple.get("sumOfWeight", Double.class), tuple.get("totalDocket", Long.class),
						tuple.get("freight", Double.class)))
				.collect(Collectors.toList());
	}
	
	public List<Booking> getBookings(String regionCode, String basis, LocalDate fromDate, LocalDate toDate) {
	    return bookingRepository.findBookings(regionCode, basis, java.sql.Date.valueOf(fromDate), java.sql.Date.valueOf(toDate));	    		
	
	}
	
//	Here booking region_delivery model
	
	public List<BookingRegion> getBookingRegions() {
	    List<String> regionCodes = Arrays.asList("XARO", "XMRO", "XPRO", "XKRO", "XCRO", "XNRO", "XBRO", "XHRO", "XDRO");
	    return bookingRepository.findBookingRegionsByCodes(regionCodes);
	}

	
	 public List<BookingRegion> getBookingRegionByDateRange(LocalDate startDate, LocalDate endDate) {
	        Date sqlStartDate = Date.valueOf(startDate);
	        Date sqlEndDate = Date.valueOf(endDate);

	        List<BookingRegion> regions = bookingRepository.findByDateRange(sqlStartDate, sqlEndDate);
	        List<String> regionCodes = Arrays.asList("XARO", "XMRO", "XPRO", "XKRO", "XCRO", "XNRO", "XBRO", "XHRO", "XDRO");

	        // Calculate total
	        BookingRegion total = new BookingRegion("Total", 0.0, 0L, 0.0);
	        for (BookingRegion region : regions) {
	            total.setTotalDocket(total.getTotalDocket() + region.getTotalDocket());
	            total.setSumOfWeight(total.getSumOfWeight() + region.getSumOfWeight());
	            total.setFreight(total.getFreight() + region.getFreight());
	        }

	        // Add total to the list
	        regions.add(total);

	        return regions.stream()
	                .filter(region -> regionCodes.contains(region.getRegionCode()) || "Total".equals(region.getRegionCode()))
	                .collect(Collectors.toList());
	    }
	 
	 
	 public List<Booking> getBookingsRegion(String regionCode,  LocalDate fromDate, LocalDate toDate) {
		    return bookingRepository.findBookingsRegion(regionCode, java.sql.Date.valueOf(fromDate), java.sql.Date.valueOf(toDate));	    		
		
		}
}
