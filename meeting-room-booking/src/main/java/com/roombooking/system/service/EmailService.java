package com.roombooking.system.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.roombooking.system.model.Booking;
import com.roombooking.system.repository.BookingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final BookingRepository bookingRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Send email notification when booking is created (PENDING)
    @Async
    public void sendBookingRequestEmail(Booking booking, String adminEmail) {
        try {
            log.info("Sending booking request email to admin: {}", adminEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("New Booking Request - " + booking.getMeetingTitle());
            message.setText(buildBookingRequestEmailBody(booking));

            mailSender.send(message);
            log.info("Booking request email sent successfully to: {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send booking request email: {}", e.getMessage());
        }
    }

    // Send email to employee when booking is created
    @Async
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            log.info("Sending booking confirmation email to user: {}", booking.getUser().getEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(booking.getUser().getEmail());
            message.setSubject("Booking Request Submitted - " + booking.getMeetingTitle());
            message.setText(buildBookingSubmittedEmailBody(booking));

            mailSender.send(message);
            log.info("Booking confirmation email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email: {}", e.getMessage());
        }
    }

    // Send email when booking is approved
    @Async
    public void sendBookingApprovedEmail(Booking booking) {
        try {
            log.info("Sending approval email to user: {}", booking.getUser().getEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(booking.getUser().getEmail());
            message.setSubject("Booking Approved - " + booking.getMeetingTitle());
            message.setText(buildApprovalEmailBody(booking));

            mailSender.send(message);
            log.info("Approval email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send approval email: {}", e.getMessage());
        }
    }

    // Send email when booking is rejected
    @Async
    public void sendBookingRejectedEmail(Booking booking) {
        try {
            log.info("Sending rejection email to user: {}", booking.getUser().getEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(booking.getUser().getEmail());
            message.setSubject("Booking Rejected - " + booking.getMeetingTitle());
            message.setText(buildRejectionEmailBody(booking));

            mailSender.send(message);
            log.info("Rejection email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send rejection email: {}", e.getMessage());
        }
    }

    // Send reminder email before meeting starts
    @Async
    public void sendMeetingReminderEmail(Booking booking) {
        try {
            log.info("Sending meeting reminder to user: {}", booking.getUser().getEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(booking.getUser().getEmail());
            message.setSubject("Reminder: Meeting in 15 minutes - " + booking.getMeetingTitle());
            message.setText(buildReminderEmailBody(booking));

            mailSender.send(message);
            log.info("Reminder email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send reminder email: {}", e.getMessage());
        }
    }

    // Email body templates
    private String buildBookingRequestEmailBody(Booking booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return String.format("""
                Dear Admin,
                
                A new booking request has been submitted and requires your approval.
                
                Booking Details:
                ================
                Meeting Title: %s
                Requested By: %s
                Room: %s
                Date: %s
                Time: %s to %s
                Attendees: %d
                
                Description:
                %s
                
                Please log in to the system to approve or reject this booking.
                
                Best regards,
                Meeting Room Booking System
                """,
                booking.getMeetingTitle(),
                booking.getUser().getFullName(),
                booking.getRoom().getName(),
                booking.getBookingDate().format(dateFormatter),
                booking.getStartTime().format(timeFormatter),
                booking.getEndTime().format(timeFormatter),
                booking.getAttendeesCount() != null ? booking.getAttendeesCount() : 0,
                booking.getDescription() != null ? booking.getDescription() : "N/A"
        );
    }

    private String buildBookingSubmittedEmailBody(Booking booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return String.format("""
                Dear %s,
                
                Your booking request has been submitted successfully and is waiting for admin approval.
                
                Booking Details:
                ================
                Meeting Title: %s
                Room: %s
                Date: %s
                Time: %s to %s
                Status: PENDING APPROVAL
                
                You will receive a notification once the admin reviews your request.
                
                Best regards,
                Meeting Room Booking System
                """,
                booking.getUser().getFullName(),
                booking.getMeetingTitle(),
                booking.getRoom().getName(),
                booking.getBookingDate().format(dateFormatter),
                booking.getStartTime().format(timeFormatter),
                booking.getEndTime().format(timeFormatter)
        );
    }

    private String buildApprovalEmailBody(Booking booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return String.format("""
                Dear %s,
                
                Great news! Your booking request has been APPROVED.
                
                Booking Details:
                ================
                Meeting Title: %s
                Room: %s
                Date: %s
                Time: %s to %s
                Status: CONFIRMED
                
                Approved By: %s
                Approved At: %s
                
                Please ensure you are on time for your meeting. The room will be ready for you.
                
                Best regards,
                Meeting Room Booking System
                """,
                booking.getUser().getFullName(),
                booking.getMeetingTitle(),
                booking.getRoom().getName(),
                booking.getBookingDate().format(dateFormatter),
                booking.getStartTime().format(timeFormatter),
                booking.getEndTime().format(timeFormatter),
                booking.getApprovedBy().getFullName(),
                booking.getApprovedAt().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"))
        );
    }

    private String buildRejectionEmailBody(Booking booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return String.format("""
                Dear %s,
                
                We regret to inform you that your booking request has been REJECTED.
                
                Booking Details:
                ================
                Meeting Title: %s
                Room: %s
                Date: %s
                Time: %s to %s
                Status: REJECTED
                
                Rejected By: %s
                
                Please contact the admin or try booking a different time slot.
                
                Best regards,
                Meeting Room Booking System
                """,
                booking.getUser().getFullName(),
                booking.getMeetingTitle(),
                booking.getRoom().getName(),
                booking.getBookingDate().format(dateFormatter),
                booking.getStartTime().format(timeFormatter),
                booking.getEndTime().format(timeFormatter),
                booking.getApprovedBy().getFullName()
        );
    }

    private String buildReminderEmailBody(Booking booking) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return String.format("""
                Dear %s,
                
                This is a reminder that your meeting is starting in 15 minutes!
                
                Meeting Details:
                ================
                Meeting Title: %s
                Room: %s
                Start Time: %s
                
                Please proceed to the meeting room.
                
                Best regards,
                Meeting Room Booking System
                """,
                booking.getUser().getFullName(),
                booking.getMeetingTitle(),
                booking.getRoom().getName(),
                booking.getStartTime().format(timeFormatter)
        );
    }

    // Utility method to send plain text email
    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            log.info("Sending email to: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }
    
    @Scheduled(cron = "0 */15 * * * *") // Run every 15 minutes
    @Async
    public void sendUpcomingMeetingReminders() {
        log.info("Checking for upcoming meetings to send reminders...");
        
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime fifteenMinutesLater = now.plusMinutes(15);
        
        List<Booking> upcomingBookings = bookingRepository.findUpcomingBookings(
            today, now, fifteenMinutesLater
        );
        
        log.info("Found {} upcoming meetings", upcomingBookings.size());
        
        for (Booking booking : upcomingBookings) {
            sendMeetingReminderEmail(booking);
        }
    }
}