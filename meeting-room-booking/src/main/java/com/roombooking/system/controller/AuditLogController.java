package com.roombooking.system.controller;

import com.roombooking.system.dto.ApiResponse;
import com.roombooking.system.model.AuditLog;
import com.roombooking.system.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    // Get recent audit logs (Admin only)
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentLogs() {
        try {
            List<AuditLog> logs = auditLogService.getRecentLogs(50);
            return ResponseEntity.ok(new ApiResponse(true, "Recent logs fetched", logs));
        } catch (Exception e) {
            log.error("Failed to fetch recent logs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get logs by entity type (e.g., BOOKING, ROOM)
    @GetMapping("/entity/{entityType}")
    public ResponseEntity<?> getLogsByEntityType(@PathVariable String entityType) {
        try {
            List<AuditLog> logs = auditLogService.getLogsByEntityType(entityType);
            return ResponseEntity.ok(new ApiResponse(true, "Logs fetched", logs));
        } catch (Exception e) {
            log.error("Failed to fetch logs by entity type: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get logs for specific entity (e.g., specific booking)
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<?> getLogsByEntity(@PathVariable String entityType, 
                                             @PathVariable Long entityId) {
        try {
            List<AuditLog> logs = auditLogService.getLogsByEntity(entityType, entityId);
            return ResponseEntity.ok(new ApiResponse(true, "Entity logs fetched", logs));
        } catch (Exception e) {
            log.error("Failed to fetch entity logs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get logs by date range
    @GetMapping("/date-range")
    public ResponseEntity<?> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<AuditLog> logs = auditLogService.getLogsByDateRange(start, end);
            return ResponseEntity.ok(new ApiResponse(true, "Logs fetched for date range", logs));
        } catch (Exception e) {
            log.error("Failed to fetch logs by date range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}