package com.roombooking.system.controller;

import com.roombooking.system.dto.ApiResponse;
import com.roombooking.system.dto.ApprovalRequest;
import com.roombooking.system.dto.BookingRequest;
import com.roombooking.system.dto.BookingResponse;
import com.roombooking.system.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    // Create booking (Employee)
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        try {
            log.info("Create booking request received for room ID: {}", request.roomId());
            BookingResponse response = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Booking request submitted. Waiting for admin approval.", response));
        } catch (RuntimeException e) {
            log.error("Failed to create booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Approve/Reject booking (Admin)
    @PostMapping("/approve")
    public ResponseEntity<?> approveBooking(@Valid @RequestBody ApprovalRequest request) {
        try {
            log.info("Approval request for booking ID: {}", request.bookingId());
            BookingResponse response = bookingService.approveBooking(request);
            String message = request.approve() ? "Booking approved successfully" : "Booking rejected";
            return ResponseEntity.ok(new ApiResponse(true, message, response));
        } catch (RuntimeException e) {
            log.error("Failed to process approval: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Cancel booking
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, 
                                          @RequestParam Long userId) {
        try {
            log.info("Cancel booking request for ID: {}", bookingId);
            bookingService.cancelBooking(bookingId, userId);
            return ResponseEntity.ok(new ApiResponse(true, "Booking cancelled successfully", null));
        } catch (RuntimeException e) {
            log.error("Failed to cancel booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get my bookings (Employee)
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(@RequestParam Long userId) {
        try {
            List<BookingResponse> bookings = bookingService.getMyBookings(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Bookings fetched successfully", bookings));
        } catch (RuntimeException e) {
            log.error("Failed to fetch bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get all bookings (Admin)
    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        try {
            List<BookingResponse> bookings = bookingService.getAllBookings();
            return ResponseEntity.ok(new ApiResponse(true, "All bookings fetched", bookings));
        } catch (RuntimeException e) {
            log.error("Failed to fetch bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get pending bookings (Admin)
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingBookings() {
        try {
            List<BookingResponse> bookings = bookingService.getPendingBookings();
            return ResponseEntity.ok(new ApiResponse(true, "Pending bookings fetched", bookings));
        } catch (RuntimeException e) {
            log.error("Failed to fetch pending bookings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get booking by ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        try {
            BookingResponse booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(new ApiResponse(true, "Booking fetched", booking));
        } catch (RuntimeException e) {
            log.error("Failed to fetch booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}