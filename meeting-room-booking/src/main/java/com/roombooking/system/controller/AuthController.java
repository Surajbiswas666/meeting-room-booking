package com.roombooking.system.controller;
import com.roombooking.system.dto.ApiResponse;
import com.roombooking.system.dto.AuthResponse;
import com.roombooking.system.dto.LoginRequest;
import com.roombooking.system.dto.RegisterRequest;
import com.roombooking.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Registration request received for: {}", request.username());
            AuthResponse response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Registration successful", response));
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Login request received for: {}", request.username());
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth API is working!");
    }
}