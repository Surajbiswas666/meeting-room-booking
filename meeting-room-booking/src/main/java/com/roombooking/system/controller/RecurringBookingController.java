package com.roombooking.system.controller;

import com.roombooking.system.dto.ApiResponse;
import com.roombooking.system.dto.RecurringBookingRequest;
import com.roombooking.system.dto.RecurringBookingResponse;
import com.roombooking.system.service.RecurringBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RecurringBookingController {

    private final RecurringBookingService recurringBookingService;

    // Create recurring booking
    @PostMapping
    public ResponseEntity<?> createRecurringBooking(@Valid @RequestBody RecurringBookingRequest request) {
        try {
            log.info("Create recurring booking request for room ID: {}", request.roomId());
            RecurringBookingResponse response = recurringBookingService.createRecurringBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Recurring booking created successfully", response));
        } catch (RuntimeException e) {
            log.error("Failed to create recurring booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Delete recurring booking (deactivate)
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<?> deleteRecurringBooking(@PathVariable Long ruleId, 
                                                    @RequestParam Long userId) {
        try {
            log.info("Delete recurring booking request for rule ID: {}", ruleId);
            recurringBookingService.deleteRecurringBooking(ruleId, userId);
            return ResponseEntity.ok(new ApiResponse(true, "Recurring booking deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Failed to delete recurring booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get my recurring bookings
    @GetMapping("/my-rules")
    public ResponseEntity<?> getMyRecurringBookings(@RequestParam Long userId) {
        try {
            List<RecurringBookingResponse> rules = recurringBookingService.getMyRecurringBookings(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Recurring bookings fetched", rules));
        } catch (RuntimeException e) {
            log.error("Failed to fetch recurring bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get recurring booking by ID
    @GetMapping("/{ruleId}")
    public ResponseEntity<?> getRecurringBookingById(@PathVariable Long ruleId) {
        try {
            RecurringBookingResponse rule = recurringBookingService.getRecurringBookingById(ruleId);
            return ResponseEntity.ok(new ApiResponse(true, "Recurring booking fetched", rule));
        } catch (RuntimeException e) {
            log.error("Failed to fetch recurring booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Manual trigger for scheduler (for testing)
    @PostMapping("/process-now")
    public ResponseEntity<?> processRecurringBookingsNow() {
        try {
            log.info("Manual trigger: Processing recurring bookings");
            recurringBookingService.processRecurringBookings();
            return ResponseEntity.ok(new ApiResponse(true, "Recurring bookings processed successfully", null));
        } catch (Exception e) {
            log.error("Failed to process recurring bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}