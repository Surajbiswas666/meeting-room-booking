package com.roombooking.system.dto;
public record ApiResponse(
        boolean success,
        String message,
        Object data
) {}