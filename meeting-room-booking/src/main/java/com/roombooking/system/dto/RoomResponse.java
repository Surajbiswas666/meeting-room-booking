package com.roombooking.system.dto;
public record RoomResponse(
        Long id,
        String name,
        Integer capacity,
        Integer floor,
        String amenities,
        String imageUrl,
        Boolean isActive
) {}