package com.roombooking.system.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roombooking.system.dto.RoomRequest;
import com.roombooking.system.dto.RoomResponse;
import com.roombooking.system.model.Room;
import com.roombooking.system.model.User;
import com.roombooking.system.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final AuditLogService auditLogService;
    private final UserService userService;

    @Transactional
    public RoomResponse createRoom(RoomRequest request, User admin) {
        log.info("Creating new room: {}", request.name());

        Room room = new Room();
        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setFloor(request.floor());
        room.setAmenities(request.amenities());
        room.setImageUrl(request.imageUrl());
        room.setIsActive(true);

        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully with ID: {}", savedRoom.getId());

        // AUDIT LOG with admin user (can be null for system actions)
        auditLogService.logCreate(admin, "ROOM", savedRoom.getId(), savedRoom);

        return mapToResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request, User admin) {
        log.info("Updating room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        // Store old state for audit
        String oldState = String.format("name=%s,capacity=%d", room.getName(), room.getCapacity());

        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setFloor(request.floor());
        room.setAmenities(request.amenities());
        room.setImageUrl(request.imageUrl());

        Room updatedRoom = roomRepository.save(room);
        log.info("Room updated successfully: {}", updatedRoom.getId());

        // AUDIT LOG with admin user
        auditLogService.logUpdate(admin, "ROOM", roomId, oldState, updatedRoom);

        return mapToResponse(updatedRoom);
    }

    @Transactional
    public void deleteRoom(Long roomId, User admin) {
        log.info("Deleting room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        // Soft delete
        room.setIsActive(false);
        roomRepository.save(room);
        
        // AUDIT LOG with admin user
        auditLogService.logDelete(admin, "ROOM", roomId, room);
        
        log.info("Room soft-deleted: {}", roomId);
    }

    public List<RoomResponse> getAllRooms() {
        log.info("Fetching all active rooms");
        return roomRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long roomId) {
        log.info("Fetching room ID: {}", roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));
        return mapToResponse(room);
    }

    public List<RoomResponse> searchRoomsByCapacity(Integer minCapacity) {
        log.info("Searching rooms with capacity >= {}", minCapacity);
        return roomRepository.findByCapacityGreaterThanEqualAndIsActiveTrue(minCapacity).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> searchRoomsByName(String name) {
        log.info("Searching rooms by name: {}", name);
        return roomRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Room getRoomEntity(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));
    }

    // Helper method to map entity to response
    private RoomResponse mapToResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getFloor(),
                room.getAmenities(),
                room.getImageUrl(),
                room.getIsActive()
        );
    }
    
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        return createRoom(request, null);
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request) {
        return updateRoom(roomId, request, null);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        deleteRoom(roomId, null);
    }
}