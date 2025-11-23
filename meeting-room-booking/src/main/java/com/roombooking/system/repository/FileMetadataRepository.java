package com.roombooking.system.repository;

import com.roombooking.system.model.FileMetadata;
import com.roombooking.system.model.Booking;
import com.roombooking.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    
    List<FileMetadata> findByBooking(Booking booking);
    
    List<FileMetadata> findByBookingId(Long bookingId);
    
    List<FileMetadata> findByUploadedBy(User user);
    
    Long countByBookingId(Long bookingId);
}