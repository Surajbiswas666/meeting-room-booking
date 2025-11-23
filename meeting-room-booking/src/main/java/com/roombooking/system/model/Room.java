package com.roombooking.system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity;

    @Column
    private Integer floor;

    @Column(columnDefinition = "TEXT")
    private String amenities; 

    @Column
    private String imageUrl; 

    @Column(nullable = false)
    private Boolean isActive = true; 

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}