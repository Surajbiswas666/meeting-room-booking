package com.roombooking.system.service;

import com.roombooking.system.dto.ApprovalRequest;
import com.roombooking.system.dto.BookingRequest;
import com.roombooking.system.dto.BookingResponse;
import com.roombooking.system.enums.BookingStatus;
import com.roombooking.system.enums.UserRole;
import com.roombooking.system.model.Booking;
import com.roombooking.system.model.Room;
import com.roombooking.system.model.User;
import com.roombooking.system.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for room ID: {} by user ID: {}", request.roomId(), request.userId());

        // Validate time
        if (!request.endTime().isAfter(request.startTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Get room and user
        Room room = roomService.getRoomEntity(request.roomId());
        User user = userService.getUserById(request.userId());

        // Check for conflicts with APPROVED bookings only
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.roomId(),
                request.bookingDate(),
                request.startTime(),
                request.endTime()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for this time slot");
        }

        // Create booking with PENDING status
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setMeetingTitle(request.meetingTitle());
        booking.setDescription(request.description());
        booking.setBookingDate(request.bookingDate());
        booking.setStartTime(request.startTime());
        booking.setEndTime(request.endTime());
        booking.setAttendeesCount(request.attendeesCount());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with ID: {} and status: PENDING", savedBooking.getId());
        auditLogService.logCreate(user, "BOOKING", savedBooking.getId(), savedBooking);
        // ---------------- EMAIL EVENTS ----------------

        // 1. Send email to user (confirmation of submission)
        emailService.sendBookingConfirmationEmail(savedBooking);

//        // 2. Notify admin(s): You must fetch emails of admins here
//        List<User> admins = userService.getAdmins();
//        admins.forEach(admin ->
//                emailService.sendBookingRequestEmail(savedBooking, admin.getEmail())
//        );

        return mapToResponse(savedBooking);
    }

    @Transactional
    public BookingResponse approveBooking(ApprovalRequest request) {
        log.info("Processing approval for booking ID: {}", request.bookingId());

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only PENDING bookings can be approved/rejected");
        }

        User admin = userService.getUserById(request.adminId());
        if (admin.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only admins can approve/reject bookings");
        }

        if (request.approve()) {
            // Check conflicts again before approving
            List<Booking> conflicts = bookingRepository.findConflictingBookings(
                    booking.getRoom().getId(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime()
            );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room has been booked by another approved booking");
        }

        booking.setStatus(BookingStatus.APPROVED);
        log.info("Booking ID: {} APPROVED by admin ID: {}", request.bookingId(), request.adminId());
        
        // AUDIT LOG - Approve action
        auditLogService.logApprove(admin, "BOOKING", request.bookingId());
    } else {
        booking.setStatus(BookingStatus.REJECTED);
        log.info("Booking ID: {} REJECTED by admin ID: {}", request.bookingId(), request.adminId());
        
        // AUDIT LOG - Reject action
        auditLogService.logReject(admin, "BOOKING", request.bookingId());
    }

    booking.setApprovedBy(admin);
    booking.setApprovedAt(LocalDateTime.now());

    Booking updatedBooking = bookingRepository.save(booking);
    
    // Send email notification
    if (request.approve()) {
        emailService.sendBookingApprovedEmail(updatedBooking);
    } else {
        emailService.sendBookingRejectedEmail(updatedBooking);
    }
    
    return mapToResponse(updatedBooking);
}

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        log.info("Cancelling booking ID: {} by user ID: {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User user = userService.getUserById(userId);

        if (!booking.getUser().getId().equals(userId) && user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // AUDIT LOG - Cancel action with actual user
        auditLogService.logCancel(user, "BOOKING", bookingId);
        log.info("Booking cancelled: {}", bookingId);
    }

    public List<BookingResponse> getMyBookings(Long userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        User user = userService.getUserById(userId);
        return bookingRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings (Admin)");
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getPendingBookings() {
        log.info("Fetching pending bookings");
        return bookingRepository.findByStatus(BookingStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingById(Long bookingId) {
        log.info("Fetching booking ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToResponse(booking);
    }

    // Helper method to map entity to response
    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getUser().getId(),
                booking.getUser().getFullName(),
                booking.getMeetingTitle(),
                booking.getDescription(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getAttendeesCount(),
                booking.getStatus(),
                booking.getApprovedBy() != null ? booking.getApprovedBy().getId() : null,
                booking.getApprovedBy() != null ? booking.getApprovedBy().getFullName() : null,
                booking.getApprovedAt(),
                booking.getCreatedAt()
        );
    }
}