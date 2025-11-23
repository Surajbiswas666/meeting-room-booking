package com.roombooking.system.repository;
import com.roombooking.system.model.AuditLog;
import com.roombooking.system.model.User;
import com.roombooking.system.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUser(User user);
    
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    List<AuditLog> findByEntityType(String entityType);
    
    List<AuditLog> findByAction(AuditAction action);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<AuditLog> findTop50ByOrderByTimestampDesc();
}