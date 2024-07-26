package com.booking.repository;


import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.booking.model.Booking;
import com.booking.model.BookingRegion;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, String>{
	
	
	 List<Booking> findByCustcode(String custcode);
	
	 @Query("SELECT b.bkgBasis AS bkgBasis, SUM(b.wt) AS sumOfWeight, COUNT(b.dwbno) AS totalDocket, " +
	            "SUM(b.frt) - SUM(b.cgst + b.sgst + b.igst) AS freight " +
	            "FROM Booking b " +
	            "WHERE b.regionCode = :regionCode AND b.bkgBasis IN :bkgBases " +
	            "GROUP BY b.bkgBasis")
	    List<Tuple> findBookingSummary(@Param("regionCode") String regionCode,
	                                   @Param("bkgBases") List<String> bkgBases);
	    
	  @Query("SELECT b.bkgBasis AS bkgBasis, SUM(b.wt) AS sumOfWeight, COUNT(b.dwbno) AS totalDocket, " +
	           "SUM(b.frt) - SUM(b.cgst + b.sgst + b.igst) AS freight " +
	           "FROM Booking b " +
	           "WHERE b.regionCode = :regionCode AND b.bkgBasis IN :bkgBases " +
	           "AND b.dwbdt BETWEEN :fromDate AND :toDate " +
	           "GROUP BY b.bkgBasis")
	    List<Tuple> findBookingSummary(@Param("regionCode") String regionCode,
	                                   @Param("bkgBases") List<String> bkgBases,
	                                   @Param("fromDate") Date fromDate,
	                                   @Param("toDate") Date toDate);
	  
	  @Query("SELECT b FROM Booking b WHERE b.regionCode "
	  		+ "= :regionCode AND b.bkgBasis = :basis AND b.dwbdt BETWEEN :fromDate AND :toDate")
	    List<Booking> findBookings(@Param("regionCode") String regionCode, 
	                               @Param("basis") String basis, 
	                               @Param("fromDate") Date fromDate, 
	                               @Param("toDate") Date toDate);
	  
	  
//	  this code is goes to region_for_delivery model
	  
	  @Query("SELECT new com.booking.model.BookingRegion(" +
	           "b.regionCode, " +
	           "SUM(b.wt), " +
	           "COUNT(b.dwbno), " +
	           "SUM(b.frt) - SUM(b.cgst + b.sgst + b.igst)) " +
	           "FROM Booking b " +
	           "WHERE b.regionCode IN :regionCodes " +
	           "GROUP BY b.regionCode")
	    List<BookingRegion> findBookingRegionsByCodes(@Param("regionCodes") List<String> regionCodes);
	  
	  @Query("SELECT new com.booking.model.BookingRegion(b.regionCode, " +
		       "SUM(b.wt), " +
		       "COUNT(b.dwbno), " +
		       "SUM(b.frt) - SUM(b.cgst + b.sgst + b.igst)) " +
		       "FROM Booking b " +
		       "WHERE b.dwbdt BETWEEN :startDate AND :endDate " +
		       "GROUP BY b.regionCode")
		List<BookingRegion> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	  
	  @Query("SELECT b FROM Booking b WHERE b.regionCode = :regionCode AND b.dwbdt BETWEEN :startDate AND :endDate")
	  List<Booking> findBookingsRegion(
	      @Param("regionCode") String regionCode, 
	      @Param("startDate") Date fromDate, 
	      @Param("endDate") Date toDate);

	  

}
