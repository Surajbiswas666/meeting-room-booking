package com.roombooking.system.dto;

import jakarta.validation.constraints.NotNull;

public record ApprovalRequest(
        @NotNull(message = "Booking ID is required")
        Long bookingId,
        
        @NotNull(message = "Admin ID is required")
        Long adminId,
        
        @NotNull(message = "Approval status is required (true=APPROVE, false=REJECT)")
        Boolean approve,
        
        String remarks
) {}