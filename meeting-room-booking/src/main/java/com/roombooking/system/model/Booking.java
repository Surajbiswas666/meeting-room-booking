package com.roombooking.system.model;

import com.roombooking.system.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "bookings", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "booking_date", "start_time"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Room is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Meeting title is required")
    @Column(nullable = false, length = 200)
    private String meetingTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Booking date is required")
    @Column(nullable = false)
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private LocalTime endTime;

    @Column
    private Integer attendeesCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    // For recurring bookings - link to parent rule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_rule_id")
    private RecurringRule recurringRule;

    // Track who approved/rejected
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}