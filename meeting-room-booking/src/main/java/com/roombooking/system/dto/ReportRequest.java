package com.roombooking.system.dto;

import java.time.LocalDate;

import com.roombooking.system.enums.BookingStatus;

import jakarta.validation.constraints.NotNull;

public record ReportRequest(
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        
        @NotNull(message = "End date is required")
        LocalDate endDate,
        
        Long userId,      // Optional: Filter by specific user
        Long roomId,      // Optional: Filter by specific room
        BookingStatus status  // Optional: Filter by status
) {}
