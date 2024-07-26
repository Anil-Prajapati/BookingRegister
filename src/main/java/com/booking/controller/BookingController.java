package com.booking.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.model.Booking;
import com.booking.model.BookingRegion;
import com.booking.model.BookingSummary;
import com.booking.service.BookingService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@GetMapping("/all")
	public Iterable<Booking> getAll() {
		return bookingService.getAll();
	}

	@GetMapping("/custcode/{custcode}")
	public List<Booking> geCustomerCode(@PathVariable("custcode") String custcode) {
		return bookingService.getCustcode(custcode);
	}
	
	@GetMapping("/openLink")
	public void openLink(@RequestParam("link") String link, HttpSession session, HttpServletResponse response)
			throws IOException {
		
		session.setAttribute("Audbrn", "XKRO");
		session.setAttribute("Auduser", "EXP06403");
		if ("ULIP".equals(link)) {
			String baseUrl = "http://localhost:8888/BookingRegister/summary";
			String brn = (String) session.getAttribute("Audbrn");
			String sc = (String) session.getAttribute("Auduser");
			String option = "ULIP";

			session.setAttribute("regionCode", brn);
			System.out.println("Open link Region Code "+brn);

			String query = String.format("brn=%s&sc=%s&option=%s", brn, sc, option);
			String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
			String urlWithParams = baseUrl + "?" + encodedQuery;
			// Redirect to the URL
			response.sendRedirect(urlWithParams);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid link");
		}
	}

	@GetMapping("/summary")
	public String getBookingSummary(Model model, HttpSession session) {
		String regionCode = (String) session.getAttribute("regionCode");
		System.out.println("Region Code Is That :"+regionCode);

		if (regionCode == null) {
			return "redirect:/unauthorize";
		}

		List<String> bkgBases = Arrays.asList("BOD", "FOD", "PAID", "TBB");
		List<BookingSummary> summary = bookingService.getBookingSummary(regionCode, bkgBases);
		model.addAttribute("summary", summary);
		model.addAttribute("totalDocket", summary.stream().mapToLong(BookingSummary::getTotalDocket).sum());
		model.addAttribute("totalWeight", summary.stream().mapToDouble(BookingSummary::getSumOfWeight).sum());
		model.addAttribute("totalFreight", summary.stream().mapToDouble(BookingSummary::getFreight).sum());
		return "summary";
	}

	@GetMapping("/summary/date-range")
	public String getBookingSummaryByDateRange(
			@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate, Model model) {
		model.addAttribute("fromDate", fromDate);
	    model.addAttribute("toDate", toDate);
		String regionCode = "XKRO";
		List<String> bkgBases = Arrays.asList("BOD", "FOD", "PAID", "TBB");
		List<BookingSummary> summary = bookingService.getBookingSummary(regionCode, bkgBases, fromDate, toDate);
		model.addAttribute("summary", summary);
		model.addAttribute("totalDocket", summary.stream().mapToLong(BookingSummary::getTotalDocket).sum());
		model.addAttribute("totalWeight", summary.stream().mapToDouble(BookingSummary::getSumOfWeight).sum());
		model.addAttribute("totalFreight", summary.stream().mapToDouble(BookingSummary::getFreight).sum());
		return "summary";
	}

	@GetMapping("/summary/current-month")
	public String getCurrentMonthSummary(Model model) {
		LocalDate now = LocalDate.now();
		LocalDate startOfMonth = now.withDayOfMonth(1);
		return getBookingSummaryByDateRange(startOfMonth, now, model);
	}

	@GetMapping("/summary/previous-month")
	public String getPreviousMonthSummary(Model model) {
		LocalDate now = LocalDate.now();
		LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
		LocalDate endOfLastMonth = startOfLastMonth.withDayOfMonth(startOfLastMonth.lengthOfMonth());
		return getBookingSummaryByDateRange(startOfLastMonth, endOfLastMonth, model);
	}
	
	
//	This model is delivery region 
	@GetMapping("/region")
	public String getBookingRegionSummary(Model model) {
	    List<BookingRegion> summary = bookingService.getBookingRegions();
	    model.addAttribute("regions", summary);
	    model.addAttribute("totalDocket", summary.stream().mapToLong(BookingRegion::getTotalDocket).sum());
	    model.addAttribute("totalWeight", summary.stream().mapToDouble(BookingRegion::getSumOfWeight).sum());
	    model.addAttribute("totalFreight", summary.stream().mapToDouble(BookingRegion::getFreight).sum());
	    return "region"; // Thymeleaf template name
	}


	
	@GetMapping("/region/date-range")
	public String getBookingRegionByDateRange(
	        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, 
	        Model model) {
		model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    List<BookingRegion> regions = bookingService.getBookingRegionByDateRange(startDate, endDate);
	    double totalFreight = regions.stream().filter(region -> !"Total".equals(region.getRegionCode())).mapToDouble(BookingRegion::getFreight).sum();

	    model.addAttribute("regions", regions);
	    model.addAttribute("totalDocket", regions.stream().filter(region -> !"Total".equals(region.getRegionCode())).mapToLong(BookingRegion::getTotalDocket).sum());
	    model.addAttribute("totalWeight", regions.stream().filter(region -> !"Total".equals(region.getRegionCode())).mapToDouble(BookingRegion::getSumOfWeight).sum());
	    model.addAttribute("totalFreight", totalFreight);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);

	    return "region";
	}

	@GetMapping("/region/current-month")
	public String getCurrentMonthRegion(Model model) {
		LocalDate now = LocalDate.now();
		LocalDate startOfMonth = now.withDayOfMonth(1);
		return getBookingSummaryByDateRange(startOfMonth, now, model);
	}

	@GetMapping("/region/previous-month")
	public String getPreviousMonthRegion(Model model) {
		LocalDate now = LocalDate.now();
		LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
		LocalDate endOfLastMonth = startOfLastMonth.withDayOfMonth(startOfLastMonth.lengthOfMonth());
		return getBookingSummaryByDateRange(startOfLastMonth, endOfLastMonth, model);
	}

	
	
	
	@GetMapping("/summary/excel")
	public ResponseEntity<byte[]> getBookingSummaryExcel(@RequestParam("basis") String basis,
	                                                     @RequestParam(value="fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
	                                                     @RequestParam(value="toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
	    String regionCode = "XKRO";
	    List<Booking> bookings = bookingService.getBookings(regionCode, basis, fromDate, toDate);
	   
	    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        Sheet sheet = workbook.createSheet("Booking Summary");

	        // Create header row
	        Row headerRow = sheet.createRow(0);
	        String[] headers = {"Region Code", "Controlling Code", "PAN No", "Bkgbrn", "Dwbno", "Dwbdt", "Dly Brn", "Custcode", "Cnor",
	                            "Cnee", "Odaloc", "Pkg", "Wt", "Frt", "Fov", "St", "Ops", "Fuel", "Hand", "Dacc", "Codfod", "Rbk",
	                            "Rbkhand", "Cod", "Misc", "Dkt", "Tds", "Ser", "Cess", "Oda", "Tfrt", "Declared Value", "Basis",
	                            "Cqno", "Cqdt", "Bacode", "Div Code", "Last Update", "Insert Date", "Bkg Basis", "Dly Region",
	                            "Sb Tax", "Actual Weight", "Oda Type", "Kk Cess", "Cgst", "Sgst", "Igst", "Gstin", "Purchase Order Date"};
	        for (int i = 0; i < headers.length; i++) {
	            headerRow.createCell(i).setCellValue(headers[i]);
	        }

	        // Populate rows with data
	        int rowNum = 1;
	        for (Booking booking : bookings) {
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(booking.getRegionCode());
	            row.createCell(1).setCellValue(booking.getControllingCode());
	            row.createCell(2).setCellValue(booking.getPanNo());
	            row.createCell(3).setCellValue(booking.getBkgbrn());
	            row.createCell(4).setCellValue(booking.getDwbno());
	            row.createCell(5).setCellValue(booking.getDwbdt().toString());
	            row.createCell(6).setCellValue(booking.getDlyBrn());
	            row.createCell(7).setCellValue(booking.getCustcode());
	            row.createCell(8).setCellValue(booking.getCnor());
	            row.createCell(9).setCellValue(booking.getCnee());
	            row.createCell(10).setCellValue(booking.getOdaloc());
	            row.createCell(11).setCellValue(booking.getPkg());
	            row.createCell(12).setCellValue(booking.getWt());
	            row.createCell(13).setCellValue(booking.getFrt());
	            row.createCell(14).setCellValue(booking.getFov());
	            row.createCell(15).setCellValue(booking.getSt());
	            row.createCell(16).setCellValue(booking.getOps());
	            row.createCell(17).setCellValue(booking.getFuel());
	            row.createCell(18).setCellValue(booking.getHand());
	            row.createCell(19).setCellValue(booking.getDacc());
	            row.createCell(20).setCellValue(booking.getCodfod());
	            row.createCell(21).setCellValue(booking.getRbk());
	            row.createCell(22).setCellValue(booking.getRbkhand());
	            row.createCell(23).setCellValue(booking.getCod());
	            row.createCell(24).setCellValue(booking.getMisc());
	            row.createCell(25).setCellValue(booking.getDkt());
	            row.createCell(26).setCellValue(booking.getTds());
	            row.createCell(27).setCellValue(booking.getSer());
	            row.createCell(28).setCellValue(booking.getCess());
	            row.createCell(29).setCellValue(booking.getOda());
	            row.createCell(30).setCellValue(booking.getTfrt());
	            row.createCell(31).setCellValue(booking.getDeclaredValue().toString());
	            row.createCell(32).setCellValue(booking.getBasis());
	            row.createCell(33).setCellValue(booking.getCqno());
	            row.createCell(34).setCellValue(booking.getCqdt() != null ? booking.getCqdt().toString() : "");
	            row.createCell(35).setCellValue(booking.getBacode());
	            row.createCell(36).setCellValue(booking.getDivCode());
	            row.createCell(37).setCellValue(booking.getLastUpdate() != null ? booking.getLastUpdate().toString() : "");
	            row.createCell(38).setCellValue(booking.getInsertDate() != null ? booking.getInsertDate().toString() : "");
	            row.createCell(39).setCellValue(booking.getBkgBasis());
	            row.createCell(40).setCellValue(booking.getDlyRegion());
	            row.createCell(41).setCellValue(booking.getSbTax());
	            row.createCell(42).setCellValue(booking.getActualWeight());
	            row.createCell(43).setCellValue(booking.getOdaType());
	            row.createCell(44).setCellValue(booking.getKkCess());
	            row.createCell(45).setCellValue(booking.getCgst());
	            row.createCell(46).setCellValue(booking.getSgst());
	            row.createCell(47).setCellValue(booking.getIgst());
	            row.createCell(48).setCellValue(booking.getGstin());
	            row.createCell(49).setCellValue(booking.getPurchaseOrderDate() != null ? booking.getPurchaseOrderDate().toString() : "");
	        }

	        workbook.write(out);
	        byte[] bytes = out.toByteArray();

	        HttpHeaders headersHttp = new HttpHeaders();
	        headersHttp.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=booking_register.xlsx");

	        return ResponseEntity.ok()
	                .headers(headersHttp)
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(bytes);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).build();
	    }
	}

	@GetMapping("/region/excel")
	public ResponseEntity<byte[]> getBookingRegionExcel(
			@RequestParam("regionCode") String regionCode,
	        @RequestParam(value="fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
	        @RequestParam(value="toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
	    List<Booking> bookings = bookingService.getBookingsRegion(regionCode, fromDate, toDate);
	   
	    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        Sheet sheet = workbook.createSheet("Booking Region");

	        // Create header row
	        Row headerRow = sheet.createRow(0);
	        String[] headers = {"Region Code", "Controlling Code", "PAN No", "Bkgbrn", "Dwbno", "Dwbdt", "Dly Brn", "Custcode", "Cnor",
	                            "Cnee", "Odaloc", "Pkg", "Wt", "Frt", "Fov", "St", "Ops", "Fuel", "Hand", "Dacc", "Codfod", "Rbk",
	                            "Rbkhand", "Cod", "Misc", "Dkt", "Tds", "Ser", "Cess", "Oda", "Tfrt", "Declared Value", "Basis",
	                            "Cqno", "Cqdt", "Bacode", "Div Code", "Last Update", "Insert Date", "Bkg Basis", "Dly Region",
	                            "Sb Tax", "Actual Weight", "Oda Type", "Kk Cess", "Cgst", "Sgst", "Igst", "Gstin", "Purchase Order Date"};
	        for (int i = 0; i < headers.length; i++) {
	            headerRow.createCell(i).setCellValue(headers[i]);
	        }

	        // Populate rows with data
	        int rowNum = 1;
	        for (Booking booking : bookings) {
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(booking.getRegionCode());
	            row.createCell(1).setCellValue(booking.getControllingCode());
	            row.createCell(2).setCellValue(booking.getPanNo());
	            row.createCell(3).setCellValue(booking.getBkgbrn());
	            row.createCell(4).setCellValue(booking.getDwbno());
	            row.createCell(5).setCellValue(booking.getDwbdt().toString());
	            row.createCell(6).setCellValue(booking.getDlyBrn());
	            row.createCell(7).setCellValue(booking.getCustcode());
	            row.createCell(8).setCellValue(booking.getCnor());
	            row.createCell(9).setCellValue(booking.getCnee());
	            row.createCell(10).setCellValue(booking.getOdaloc());
	            row.createCell(11).setCellValue(booking.getPkg());
	            row.createCell(12).setCellValue(booking.getWt());
	            row.createCell(13).setCellValue(booking.getFrt());
	            row.createCell(14).setCellValue(booking.getFov());
	            row.createCell(15).setCellValue(booking.getSt());
	            row.createCell(16).setCellValue(booking.getOps());
	            row.createCell(17).setCellValue(booking.getFuel());
	            row.createCell(18).setCellValue(booking.getHand());
	            row.createCell(19).setCellValue(booking.getDacc());
	            row.createCell(20).setCellValue(booking.getCodfod());
	            row.createCell(21).setCellValue(booking.getRbk());
	            row.createCell(22).setCellValue(booking.getRbkhand());
	            row.createCell(23).setCellValue(booking.getCod());
	            row.createCell(24).setCellValue(booking.getMisc());
	            row.createCell(25).setCellValue(booking.getDkt());
	            row.createCell(26).setCellValue(booking.getTds());
	            row.createCell(27).setCellValue(booking.getSer());
	            row.createCell(28).setCellValue(booking.getCess());
	            row.createCell(29).setCellValue(booking.getOda());
	            row.createCell(30).setCellValue(booking.getTfrt());
	            row.createCell(31).setCellValue(booking.getDeclaredValue().toString());
	            row.createCell(32).setCellValue(booking.getBasis());
	            row.createCell(33).setCellValue(booking.getCqno());
	            row.createCell(34).setCellValue(booking.getCqdt() != null ? booking.getCqdt().toString() : "");
	            row.createCell(35).setCellValue(booking.getBacode());
	            row.createCell(36).setCellValue(booking.getDivCode());
	            row.createCell(37).setCellValue(booking.getLastUpdate() != null ? booking.getLastUpdate().toString() : "");
	            row.createCell(38).setCellValue(booking.getInsertDate() != null ? booking.getInsertDate().toString() : "");
	            row.createCell(39).setCellValue(booking.getBkgBasis());
	            row.createCell(40).setCellValue(booking.getDlyRegion());
	            row.createCell(41).setCellValue(booking.getSbTax());
	            row.createCell(42).setCellValue(booking.getActualWeight());
	            row.createCell(43).setCellValue(booking.getOdaType());
	            row.createCell(44).setCellValue(booking.getKkCess());
	            row.createCell(45).setCellValue(booking.getCgst());
	            row.createCell(46).setCellValue(booking.getSgst());
	            row.createCell(47).setCellValue(booking.getIgst());
	            row.createCell(48).setCellValue(booking.getGstin());
	            row.createCell(49).setCellValue(booking.getPurchaseOrderDate() != null ? booking.getPurchaseOrderDate().toString() : "");
	        }

	        workbook.write(out);
	        byte[] bytes = out.toByteArray();

	        HttpHeaders headersHttp = new HttpHeaders();
	        headersHttp.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=booking_register.xlsx");

	        return ResponseEntity.ok()
	                .headers(headersHttp)
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(bytes);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).build();
	    }
	}
	
	@GetMapping("/unauthorize")
	public String unauthorizePage() {
		return "unauthorize";
	}

}
