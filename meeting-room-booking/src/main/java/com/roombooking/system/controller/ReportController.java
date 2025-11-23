package com.roombooking.system.controller;

import com.roombooking.system.dto.AnalyticsSummary;
import com.roombooking.system.dto.ApiResponse;
import com.roombooking.system.dto.ReportRequest;
import com.roombooking.system.dto.RoomUtilizationStats;
import com.roombooking.system.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    // Generate and download PDF report
    @PostMapping("/bookings/pdf")
    public ResponseEntity<?> generateBookingsReport(@Valid @RequestBody ReportRequest request) {
        try {
            log.info("PDF report request from {} to {}", request.startDate(), request.endDate());
            
            byte[] pdfBytes = reportService.generateBookingsReport(request);
            
            // Create filename with date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String filename = "bookings_report_" + 
                            request.startDate().format(formatter) + "_to_" + 
                            request.endDate().format(formatter) + ".pdf";

            Resource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (IOException e) {
            log.error("Failed to generate PDF report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to generate report: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            log.error("Report generation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get analytics summary (for admin dashboard)
    @GetMapping("/analytics/summary")
    public ResponseEntity<?> getAnalyticsSummary() {
        try {
            AnalyticsSummary summary = reportService.getAnalyticsSummary();
            return ResponseEntity.ok(new ApiResponse(true, "Analytics fetched successfully", summary));
        } catch (Exception e) {
            log.error("Failed to fetch analytics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get room utilization statistics
    @GetMapping("/analytics/room-utilization")
    public ResponseEntity<?> getRoomUtilization() {
        try {
            List<RoomUtilizationStats> stats = reportService.getRoomUtilization();
            return ResponseEntity.ok(new ApiResponse(true, "Room utilization stats fetched", stats));
        } catch (Exception e) {
            log.error("Failed to fetch room utilization: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Quick report endpoints for common use cases
    
    // Get report for current month
    @GetMapping("/bookings/current-month")
    public ResponseEntity<?> getCurrentMonthReport() {
        try {
            LocalDate startDate = LocalDate.now().withDayOfMonth(1);
            LocalDate endDate = LocalDate.now().withDayOfMonth(
                    LocalDate.now().lengthOfMonth());
            
            ReportRequest request = new ReportRequest(startDate, endDate, null, null, null);
            byte[] pdfBytes = reportService.generateBookingsReport(request);
            
            String filename = "bookings_current_month.pdf";
            Resource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to generate current month report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get report for last 7 days
    @GetMapping("/bookings/last-week")
    public ResponseEntity<?> getLastWeekReport() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(7);
            
            ReportRequest request = new ReportRequest(startDate, endDate, null, null, null);
            byte[] pdfBytes = reportService.generateBookingsReport(request);
            
            String filename = "bookings_last_7_days.pdf";
            Resource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to generate last week report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get user-specific report
    @GetMapping("/bookings/user/{userId}/pdf")
    public ResponseEntity<?> getUserBookingsReport(@PathVariable Long userId,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            ReportRequest request = new ReportRequest(start, end, userId, null, null);
            byte[] pdfBytes = reportService.generateBookingsReport(request);
            
            String filename = "user_" + userId + "_bookings_report.pdf";
            Resource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            log.error("Failed to generate user report: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}