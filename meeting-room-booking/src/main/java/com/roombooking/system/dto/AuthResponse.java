package com.roombooking.system.dto;

import com.roombooking.system.enums.UserRole;

public record AuthResponse(
        Long userId,
        String username,
        String email,
        String fullName,
        UserRole role,
        String message
) {}