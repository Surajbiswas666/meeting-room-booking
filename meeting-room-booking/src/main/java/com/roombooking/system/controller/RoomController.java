package com.roombooking.system.controller;

import com.roombooking.system.dto.ApiResponse;
import com.roombooking.system.dto.RoomRequest;
import com.roombooking.system.dto.RoomResponse;
import com.roombooking.system.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    // Create room (Admin only - add manual check or handle in frontend)
    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomRequest request) {
        try {
            log.info("Create room request received: {}", request.name());
            RoomResponse response = roomService.createRoom(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Room created successfully", response));
        } catch (RuntimeException e) {
            log.error("Failed to create room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Update room (Admin only)
    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId, 
                                       @Valid @RequestBody RoomRequest request) {
        try {
            log.info("Update room request for ID: {}", roomId);
            RoomResponse response = roomService.updateRoom(roomId, request);
            return ResponseEntity.ok(new ApiResponse(true, "Room updated successfully", response));
        } catch (RuntimeException e) {
            log.error("Failed to update room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Delete room (Admin only)
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId) {
        try {
            log.info("Delete room request for ID: {}", roomId);
            roomService.deleteRoom(roomId);
            return ResponseEntity.ok(new ApiResponse(true, "Room deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Failed to delete room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get all rooms
    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        try {
            List<RoomResponse> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(new ApiResponse(true, "Rooms fetched successfully", rooms));
        } catch (RuntimeException e) {
            log.error("Failed to fetch rooms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get room by ID
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable Long roomId) {
        try {
            RoomResponse room = roomService.getRoomById(roomId);
            return ResponseEntity.ok(new ApiResponse(true, "Room fetched successfully", room));
        } catch (RuntimeException e) {
            log.error("Failed to fetch room: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Search rooms by capacity
    @GetMapping("/search/capacity")
    public ResponseEntity<?> searchByCapacity(@RequestParam Integer minCapacity) {
        try {
            List<RoomResponse> rooms = roomService.searchRoomsByCapacity(minCapacity);
            return ResponseEntity.ok(new ApiResponse(true, "Rooms found", rooms));
        } catch (RuntimeException e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Search rooms by name
    @GetMapping("/search/name")
    public ResponseEntity<?> searchByName(@RequestParam String name) {
        try {
            List<RoomResponse> rooms = roomService.searchRoomsByName(name);
            return ResponseEntity.ok(new ApiResponse(true, "Rooms found", rooms));
        } catch (RuntimeException e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}