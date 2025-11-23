package com.roombooking.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roombooking.system.enums.AuditAction;
import com.roombooking.system.model.AuditLog;
import com.roombooking.system.model.User;
import com.roombooking.system.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    @Transactional
    public void log(User user, String entityType, Long entityId, AuditAction action, Object oldValue, Object newValue) {
        try {
            log.info("Creating audit log: user={}, entity={}, action={}", 
                    user != null ? user.getUsername() : "SYSTEM", entityType, action);

            AuditLog auditLog = new AuditLog();
            auditLog.setUser(user);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setAction(action);
            
            // Convert objects to JSON strings
            if (oldValue != null) {
                auditLog.setOldValue(objectMapper.writeValueAsString(oldValue));
            }
            if (newValue != null) {
                auditLog.setNewValue(objectMapper.writeValueAsString(newValue));
            }
            
            // You can add IP address tracking if needed
            // auditLog.setIpAddress(getClientIP());

            auditLogRepository.save(auditLog);
            log.info("Audit log created successfully: id={}", auditLog.getId());
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage(), e);
            // Don't throw exception - audit logging should never break the main flow
        }
    }

    // Simplified logging methods
    @Async
    public void logCreate(User user, String entityType, Long entityId, Object entity) {
        log(user, entityType, entityId, AuditAction.CREATE, null, entity);
    }

    @Async
    public void logUpdate(User user, String entityType, Long entityId, Object oldEntity, Object newEntity) {
        log(user, entityType, entityId, AuditAction.UPDATE, oldEntity, newEntity);
    }

    @Async
    public void logDelete(User user, String entityType, Long entityId, Object entity) {
        log(user, entityType, entityId, AuditAction.DELETE, entity, null);
    }

    @Async
    public void logApprove(User user, String entityType, Long entityId) {
        log(user, entityType, entityId, AuditAction.APPROVE, null, null);
    }

    @Async
    public void logReject(User user, String entityType, Long entityId) {
        log(user, entityType, entityId, AuditAction.REJECT, null, null);
    }

    @Async
    public void logCancel(User user, String entityType, Long entityId) {
        log(user, entityType, entityId, AuditAction.CANCEL, null, null);
    }

    // Retrieve audit logs
    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType);
    }

    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getLogsByUser(User user) {
        return auditLogRepository.findByUser(user);
    }

    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
}