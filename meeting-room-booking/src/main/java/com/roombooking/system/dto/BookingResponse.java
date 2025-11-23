package com.roombooking.system.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.roombooking.system.enums.BookingStatus;

public record BookingResponse(
        Long id,
        Long roomId,
        String roomName,
        Long userId,
        String userName,
        String meetingTitle,
        String description,
        LocalDate bookingDate,
        LocalTime startTime,
        LocalTime endTime,
        Integer attendeesCount,
        BookingStatus status,
        Long approvedBy,
        String approvedByName,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {}