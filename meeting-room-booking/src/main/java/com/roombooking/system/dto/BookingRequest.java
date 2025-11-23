package com.roombooking.system.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "Room ID is required")
        Long roomId,
        
        @NotNull(message = "User ID is required")
        Long userId,
        
        @NotBlank(message = "Meeting title is required")
        String meetingTitle,
        
        String description,
        
        @NotNull(message = "Booking date is required")
        LocalDate bookingDate,
        
        @NotNull(message = "Start time is required")
        LocalTime startTime,
        
        @NotNull(message = "End time is required")
        LocalTime endTime,
        
        Integer attendeesCount
) {}

