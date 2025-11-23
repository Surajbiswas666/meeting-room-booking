package com.roombooking.system.model;
import com.roombooking.system.enums.RecurrenceFrequency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "recurring_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotBlank(message = "Meeting title is required")
    @Column(nullable = false, length = 200)
    private String meetingTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate; 

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate; 

    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurrenceFrequency frequency;

 
    @Column(length = 50)
    private String daysOfWeek;

    @Column
    private Integer attendeesCount;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}