package com.roombooking.system.model;
import com.roombooking.system.enums.AuditAction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Entity type is required")
    @Column(nullable = false, length = 50)
    private String entityType; 

    @NotNull(message = "Entity ID is required")
    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;

    @Column(columnDefinition = "TEXT")
    private String oldValue; 

    @Column(columnDefinition = "TEXT")
    private String newValue; 

    @Column(length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}