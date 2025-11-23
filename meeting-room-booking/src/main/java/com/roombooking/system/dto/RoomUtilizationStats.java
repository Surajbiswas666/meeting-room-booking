package com.roombooking.system.dto;
public record RoomUtilizationStats(
        Long roomId,
        String roomName,
        int totalBookings,
        int approvedBookings,
        double utilizationPercentage
) {}
