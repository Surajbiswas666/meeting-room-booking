package com.roombooking.system.service;

import com.roombooking.system.dto.RoomRequest;
import com.roombooking.system.dto.RoomResponse;
import com.roombooking.system.model.Room;
import com.roombooking.system.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
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

        return mapToResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request) {
        log.info("Updating room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        room.setName(request.name());
        room.setCapacity(request.capacity());
        room.setFloor(request.floor());
        room.setAmenities(request.amenities());
        room.setImageUrl(request.imageUrl());

        Room updatedRoom = roomRepository.save(room);
        log.info("Room updated successfully: {}", updatedRoom.getId());

        return mapToResponse(updatedRoom);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        log.info("Deleting room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + roomId));

        // Soft delete
        room.setIsActive(false);
        roomRepository.save(room);
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
}