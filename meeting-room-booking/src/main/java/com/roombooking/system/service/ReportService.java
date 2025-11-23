package com.roombooking.system.service;

import com.roombooking.system.dto.AnalyticsSummary;
import com.roombooking.system.dto.ReportRequest;
import com.roombooking.system.dto.RoomUtilizationStats;
import com.roombooking.system.enums.BookingStatus;
import com.roombooking.system.model.Booking;
import com.roombooking.system.model.Room;
import com.roombooking.system.repository.BookingRepository;
import com.roombooking.system.repository.RoomRepository;
import com.roombooking.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    // Generate PDF report for bookings
    public byte[] generateBookingsReport(ReportRequest request) throws IOException {
        log.info("Generating PDF report from {} to {}", request.startDate(), request.endDate());

        // Validate dates
        if (request.endDate().isBefore(request.startDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        // Fetch bookings
        List<Booking> bookings = bookingRepository.findByDateRange(request.startDate(), request.endDate());

        // Apply filters
        if (request.userId() != null) {
            bookings = bookings.stream()
                    .filter(b -> b.getUser().getId().equals(request.userId()))
                    .collect(Collectors.toList());
        }

        if (request.roomId() != null) {
            bookings = bookings.stream()
                    .filter(b -> b.getRoom().getId().equals(request.roomId()))
                    .collect(Collectors.toList());
        }

        if (request.status() != null) {
            bookings = bookings.stream()
                    .filter(b -> b.getStatus() == request.status())
                    .collect(Collectors.toList());
        }

        // Sort by date
        bookings.sort(Comparator.comparing(Booking::getBookingDate)
                .thenComparing(Booking::getStartTime));

        log.info("Found {} bookings for report", bookings.size());

        return createPDF(bookings, request);
    }

    // Create PDF document
    private byte[] createPDF(List<Booking> bookings, ReportRequest request) throws IOException {
        PDDocument document = new PDDocument();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Add first page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Title
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Meeting Room Booking Report");
            contentStream.endText();

            // Date range
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText("Period: " + request.startDate().format(formatter) + 
                                   " to " + request.endDate().format(formatter));
            contentStream.endText();

            // Summary
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.newLineAtOffset(50, 710);
            contentStream.showText("Total Bookings: " + bookings.size());
            contentStream.endText();

            // Table header
            float yPosition = 680;
            float margin = 50;
            float tableWidth = 500;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            
            // Draw header
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Date");
            contentStream.newLineAtOffset(80, 0);
            contentStream.showText("Time");
            contentStream.newLineAtOffset(80, 0);
            contentStream.showText("Room");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("Meeting Title");
            contentStream.newLineAtOffset(150, 0);
            contentStream.showText("Status");
            contentStream.endText();

            // Draw line under header
            yPosition -= 5;
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(margin + tableWidth, yPosition);
            contentStream.stroke();

            yPosition -= 20;

            // Table rows
            contentStream.setFont(PDType1Font.HELVETICA, 9);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            int rowCount = 0;
            for (Booking booking : bookings) {
                if (yPosition < 50) {
                    // Close current page and create new one
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = 750;
                    contentStream.setFont(PDType1Font.HELVETICA, 9);
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                
                // Date
                contentStream.showText(booking.getBookingDate().format(dateFormatter));
                contentStream.newLineAtOffset(80, 0);
                
                // Time
                contentStream.showText(booking.getStartTime().format(timeFormatter) + "-" + 
                                      booking.getEndTime().format(timeFormatter));
                contentStream.newLineAtOffset(80, 0);
                
                // Room (truncate if too long)
                String roomName = booking.getRoom().getName();
                if (roomName.length() > 12) {
                    roomName = roomName.substring(0, 12) + "..";
                }
                contentStream.showText(roomName);
                contentStream.newLineAtOffset(100, 0);
                
                // Meeting title (truncate if too long)
                String title = booking.getMeetingTitle();
                if (title.length() > 20) {
                    title = title.substring(0, 20) + "..";
                }
                contentStream.showText(title);
                contentStream.newLineAtOffset(150, 0);
                
                // Status
                contentStream.showText(booking.getStatus().toString());
                
                contentStream.endText();

                yPosition -= 20;
                rowCount++;
            }

            // Footer
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(50, 30);
            contentStream.showText("Generated on: " + new Date());
            contentStream.endText();

            contentStream.close();

            // Save to byte array
            document.save(baos);
            log.info("PDF report generated successfully with {} bookings", bookings.size());

        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    // Get analytics summary for dashboard
    public AnalyticsSummary getAnalyticsSummary() {
        log.info("Generating analytics summary");

        List<Booking> allBookings = bookingRepository.findAll();
        
        int totalBookings = allBookings.size();
        int pending = (int) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count();
        int approved = (int) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).count();
        int rejected = (int) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.REJECTED).count();
        int cancelled = (int) allBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();

        int totalRooms = (int) roomRepository.findByIsActiveTrue().size();
        int activeUsers = (int) userRepository.findByIsActiveTrue().size();

        // Find most booked room
        String mostBookedRoom = "N/A";
        if (!allBookings.isEmpty()) {
            Map<String, Long> roomBookingCount = allBookings.stream()
                    .collect(Collectors.groupingBy(
                            b -> b.getRoom().getName(),
                            Collectors.counting()
                    ));
            mostBookedRoom = Collections.max(roomBookingCount.entrySet(), Map.Entry.comparingByValue())
                    .getKey();
        }

        // Find peak booking time
        String peakTime = "N/A";
        if (!allBookings.isEmpty()) {
            Map<String, Long> timeSlotCount = allBookings.stream()
                    .collect(Collectors.groupingBy(
                            b -> b.getStartTime().getHour() + ":00",
                            Collectors.counting()
                    ));
            peakTime = Collections.max(timeSlotCount.entrySet(), Map.Entry.comparingByValue())
                    .getKey();
        }

        return new AnalyticsSummary(
                totalBookings,
                pending,
                approved,
                rejected,
                cancelled,
                totalRooms,
                activeUsers,
                mostBookedRoom,
                peakTime
        );
    }

    // Get room utilization statistics
    public List<RoomUtilizationStats> getRoomUtilization() {
        log.info("Calculating room utilization statistics");

        List<Room> rooms = roomRepository.findByIsActiveTrue();
        List<RoomUtilizationStats> stats = new ArrayList<>();

        for (Room room : rooms) {
            List<Booking> roomBookings = bookingRepository.findByRoom(room);
            int totalBookings = roomBookings.size();
            int approvedBookings = (int) roomBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                    .count();

            // Simple utilization calculation (could be more sophisticated)
            double utilization = totalBookings > 0 ? (approvedBookings * 100.0 / totalBookings) : 0.0;

            stats.add(new RoomUtilizationStats(
                    room.getId(),
                    room.getName(),
                    totalBookings,
                    approvedBookings,
                    Math.round(utilization * 100.0) / 100.0
            ));
        }

        stats.sort(Comparator.comparing(RoomUtilizationStats::totalBookings).reversed());
        return stats;
    }
}