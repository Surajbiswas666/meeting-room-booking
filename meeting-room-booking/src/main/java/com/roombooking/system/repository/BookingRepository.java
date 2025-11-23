package com.roombooking.system.repository;
import com.roombooking.system.model.Booking;
import com.roombooking.system.model.Room;
import com.roombooking.system.model.User;
import com.roombooking.system.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUser(User user);
    
    List<Booking> findByUserAndStatus(User user, BookingStatus status);
    
    List<Booking> findByRoom(Room room);
    
    List<Booking> findByStatus(BookingStatus status);
    
    List<Booking> findByBookingDate(LocalDate date);
    
    List<Booking> findByRoomAndBookingDate(Room room, LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN :startDate AND :endDate")
    List<Booking> findByDateRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.bookingDate = :date " +
           "AND b.status = 'APPROVED' " +
           "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                          @Param("date") LocalDate date,
                                          @Param("startTime") LocalTime startTime,
                                          @Param("endTime") LocalTime endTime);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate = :date " +
           "AND b.startTime BETWEEN :startTime AND :endTime " +
           "AND b.status = 'APPROVED'")
    List<Booking> findUpcomingBookings(@Param("date") LocalDate date,
                                       @Param("startTime") LocalTime startTime,
                                       @Param("endTime") LocalTime endTime);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status = 'APPROVED'")
    Long countByRoomId(@Param("roomId") Long roomId);
}