package com.roombooking.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roombooking.system.dto.RecurringBookingRequest;
import com.roombooking.system.dto.RecurringBookingResponse;
import com.roombooking.system.enums.BookingStatus;
import com.roombooking.system.enums.RecurrenceFrequency;
import com.roombooking.system.model.Booking;
import com.roombooking.system.model.RecurringRule;
import com.roombooking.system.model.Room;
import com.roombooking.system.model.User;
import com.roombooking.system.repository.BookingRepository;
import com.roombooking.system.repository.RecurringRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringBookingService {

    private final RecurringRuleRepository recurringRuleRepository;
    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public RecurringBookingResponse createRecurringBooking(RecurringBookingRequest request) {
        log.info("Creating recurring booking for user ID: {}", request.userId());

        // Validate dates
        if (!request.endDate().isAfter(request.startDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        // Validate time
        if (!request.endTime().isAfter(request.startTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Get room and user
        Room room = roomService.getRoomEntity(request.roomId());
        User user = userService.getUserById(request.userId());

        // Validate frequency and days
        if (request.frequency() == RecurrenceFrequency.WEEKLY) {
            if (request.daysOfWeek() == null || request.daysOfWeek().isEmpty()) {
                throw new RuntimeException("Days of week are required for WEEKLY frequency");
            }
        }

        // Create recurring rule
        RecurringRule rule = new RecurringRule();
        rule.setUser(user);
        rule.setRoom(room);
        rule.setMeetingTitle(request.meetingTitle());
        rule.setDescription(request.description());
        rule.setStartDate(request.startDate());
        rule.setEndDate(request.endDate());
        rule.setStartTime(request.startTime());
        rule.setEndTime(request.endTime());
        rule.setFrequency(request.frequency());
        rule.setAttendeesCount(request.attendeesCount());
        rule.setIsActive(true);

        // Convert days of week to JSON string
        if (request.daysOfWeek() != null && !request.daysOfWeek().isEmpty()) {
            try {
                rule.setDaysOfWeek(objectMapper.writeValueAsString(request.daysOfWeek()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process days of week");
            }
        }

        RecurringRule savedRule = recurringRuleRepository.save(rule);
        log.info("Recurring rule created with ID: {}", savedRule.getId());

        return mapToResponse(savedRule, 0);
    }

    @Transactional
    public void deleteRecurringBooking(Long ruleId, Long userId) {
        log.info("Deleting recurring rule ID: {}", ruleId);

        RecurringRule rule = recurringRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Recurring rule not found"));

        User user = userService.getUserById(userId);

        // Only owner can delete
        if (!rule.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own recurring bookings");
        }

        // Soft delete
        rule.setIsActive(false);
        recurringRuleRepository.save(rule);
        log.info("Recurring rule deactivated: {}", ruleId);
    }

    public List<RecurringBookingResponse> getMyRecurringBookings(Long userId) {
        log.info("Fetching recurring bookings for user ID: {}", userId);
        User user = userService.getUserById(userId);
        
        return recurringRuleRepository.findByUserAndIsActiveTrue(user).stream()
                .map(rule -> {
                    int bookingsCount = countBookingsForRule(rule.getId());
                    return mapToResponse(rule, bookingsCount);
                })
                .collect(Collectors.toList());
    }

    public RecurringBookingResponse getRecurringBookingById(Long ruleId) {
        RecurringRule rule = recurringRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Recurring rule not found"));
        int bookingsCount = countBookingsForRule(ruleId);
        return mapToResponse(rule, bookingsCount);
    }

    // Scheduled job to create bookings from recurring rules
    // Runs every day at 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void processRecurringBookings() {
        log.info("Starting scheduled job to process recurring bookings");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7); // Create bookings for next 7 days

        List<RecurringRule> activeRules = recurringRuleRepository.findActiveRulesForDate(today);
        log.info("Found {} active recurring rules", activeRules.size());

        int bookingsCreated = 0;

        for (RecurringRule rule : activeRules) {
            try {
                List<LocalDate> datesToBook = calculateBookingDates(rule, today, endDate);
                
                for (LocalDate date : datesToBook) {
                    // Check if booking already exists
                    List<Booking> existing = bookingRepository.findByRoomAndBookingDate(rule.getRoom(), date);
                    boolean alreadyExists = existing.stream()
                            .anyMatch(b -> b.getRecurringRule() != null && 
                                          b.getRecurringRule().getId().equals(rule.getId()) &&
                                          b.getStartTime().equals(rule.getStartTime()));

                    if (!alreadyExists) {
                        createBookingFromRule(rule, date);
                        bookingsCreated++;
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process recurring rule ID {}: {}", rule.getId(), e.getMessage());
            }
        }

        log.info("Scheduled job completed. Created {} bookings", bookingsCreated);
    }

    // Calculate which dates to create bookings for based on frequency
    private List<LocalDate> calculateBookingDates(RecurringRule rule, LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start.isBefore(rule.getStartDate()) ? rule.getStartDate() : start;

        while (!current.isAfter(end) && !current.isAfter(rule.getEndDate())) {
            if (shouldCreateBookingOnDate(rule, current)) {
                dates.add(current);
            }
            current = current.plusDays(1);
        }

        return dates;
    }

    // Check if booking should be created on a specific date
    private boolean shouldCreateBookingOnDate(RecurringRule rule, LocalDate date) {
        return switch (rule.getFrequency()) {
            case DAILY -> true;
            case WEEKLY -> {
                if (rule.getDaysOfWeek() == null) {
                    yield false;
                }
                try {
                    List<Integer> days = objectMapper.readValue(rule.getDaysOfWeek(), List.class);
                    int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
                    yield days.contains(dayOfWeek);
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse days of week: {}", e.getMessage());
                    yield false;
                }
            }
            case MONTHLY -> date.getDayOfMonth() == rule.getStartDate().getDayOfMonth();
        };
    }

    // Create a booking from recurring rule
    private void createBookingFromRule(RecurringRule rule, LocalDate date) {
        log.info("Creating booking from rule ID: {} for date: {}", rule.getId(), date);

        // Check for conflicts
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                rule.getRoom().getId(),
                date,
                rule.getStartTime(),
                rule.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            log.warn("Conflict detected for recurring booking on {}, skipping", date);
            return;
        }

        Booking booking = new Booking();
        booking.setRoom(rule.getRoom());
        booking.setUser(rule.getUser());
        booking.setMeetingTitle(rule.getMeetingTitle());
        booking.setDescription(rule.getDescription());
        booking.setBookingDate(date);
        booking.setStartTime(rule.getStartTime());
        booking.setEndTime(rule.getEndTime());
        booking.setAttendeesCount(rule.getAttendeesCount());
        booking.setStatus(BookingStatus.PENDING); // Still needs admin approval
        booking.setRecurringRule(rule);

        bookingRepository.save(booking);
        log.info("Booking created from recurring rule");
    }

    // Count how many bookings created from this rule
    private int countBookingsForRule(Long ruleId) {
        RecurringRule rule = recurringRuleRepository.findById(ruleId).orElse(null);
        if (rule == null) return 0;
        
        List<Booking> bookings = bookingRepository.findByRoom(rule.getRoom());
        return (int) bookings.stream()
                .filter(b -> b.getRecurringRule() != null && b.getRecurringRule().getId().equals(ruleId))
                .count();
    }

    // Helper method to map entity to response
    private RecurringBookingResponse mapToResponse(RecurringRule rule, int bookingsCreated) {
        return new RecurringBookingResponse(
                rule.getId(),
                rule.getRoom().getId(),
                rule.getRoom().getName(),
                rule.getUser().getId(),
                rule.getUser().getFullName(),
                rule.getMeetingTitle(),
                rule.getDescription(),
                rule.getStartDate(),
                rule.getEndDate(),
                rule.getStartTime(),
                rule.getEndTime(),
                rule.getFrequency(),
                rule.getDaysOfWeek(),
                rule.getAttendeesCount(),
                rule.getIsActive(),
                bookingsCreated
        );
    }
}