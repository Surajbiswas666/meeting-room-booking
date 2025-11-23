package com.roombooking.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MeetingRoomBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetingRoomBookingApplication.class, args);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  Meeting Room Booking System Started Successfully!");
        System.out.println("=".repeat(60));
        System.out.println("  API Base URL:  http://localhost:8080/api");
        System.out.println("  H2 Console:    http://localhost:8080/h2-console");
        System.out.println("  Health Check:  http://localhost:8080/actuator/health");
        System.out.println("=".repeat(60) + "\n");
    }
}