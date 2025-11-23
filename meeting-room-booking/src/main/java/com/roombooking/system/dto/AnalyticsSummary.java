package com.roombooking.system.dto;
public record AnalyticsSummary(
        int totalBookings,
        int pendingBookings,
        int approvedBookings,
        int rejectedBookings,
        int cancelledBookings,
        int totalRooms,
        int activeUsers,
        String mostBookedRoom,
        String peakBookingTime
) {}