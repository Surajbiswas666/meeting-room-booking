package com.roombooking.system.service;

import com.roombooking.system.dto.AuthResponse;
import com.roombooking.system.dto.LoginRequest;
import com.roombooking.system.dto.RegisterRequest;
import com.roombooking.system.enums.UserRole;
import com.roombooking.system.model.User;
import com.roombooking.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.username());

        // Check if username exists
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setDepartment(request.department());
        user.setPhone(request.phone());
        user.setRole(request.role());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole(),
                "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.username());

        // Find user
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new RuntimeException("User account is disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        log.info("User logged in successfully: {}", user.getUsername());

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                "Login successful"
        );
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}