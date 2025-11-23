package com.roombooking.system.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.roombooking.system.enums.RecurrenceFrequency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecurringBookingRequest(
        @NotNull(message = "Room ID is required")
        Long roomId,
        
        @NotNull(message = "User ID is required")
        Long userId,
        
        @NotBlank(message = "Meeting title is required")
        String meetingTitle,
        
        String description,
        
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        
        @NotNull(message = "End date is required")
        LocalDate endDate,
        
        @NotNull(message = "Start time is required")
        LocalTime startTime,
        
        @NotNull(message = "End time is required")
        LocalTime endTime,
        
        @NotNull(message = "Frequency is required")
        RecurrenceFrequency frequency, // DAILY, WEEKLY, MONTHLY
        
        List<Integer> daysOfWeek, // For WEEKLY: [1,3,5] = Mon, Wed, Fri (1=Mon, 7=Sun)
        
        Integer attendeesCount
) {}