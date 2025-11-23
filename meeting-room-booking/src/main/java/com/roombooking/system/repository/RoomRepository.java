package com.roombooking.system.repository;

import com.roombooking.system.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByIsActiveTrue();
    
    List<Room> findByFloorAndIsActiveTrue(Integer floor);
    
    List<Room> findByCapacityGreaterThanEqualAndIsActiveTrue(Integer capacity);
    
    List<Room> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}