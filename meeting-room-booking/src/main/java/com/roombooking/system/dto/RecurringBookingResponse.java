package com.roombooking.system.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.roombooking.system.enums.RecurrenceFrequency;

public record RecurringBookingResponse(
        Long id,
        Long roomId,
        String roomName,
        Long userId,
        String userName,
        String meetingTitle,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        RecurrenceFrequency frequency,
        String daysOfWeek, // JSON string
        Integer attendeesCount,
        Boolean isActive,
        Integer bookingsCreated // Number of bookings created from this rule
) {}