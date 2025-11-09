package com.bookfast.backend.common.service;

import com.bookfast.backend.common.notification.EmailService;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.repository.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReminderService {
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public ReminderService(BookingRepository bookingRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    /**
     * Scheduled task to send reminders 24 hours before appointments
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Run at the start of every hour
    public void send24HourReminders() {
        try {
            System.out.println("🔔 [ReminderService] Running 24-hour reminder check at: " + LocalDateTime.now());
            
            // Calculate time window: 24 hours from now (+/- 1 hour for flexibility)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderStart = now.plusHours(23);
            LocalDateTime reminderEnd = now.plusHours(25);
            
            // Find bookings in the 24-hour window
            List<Booking> upcomingBookings = bookingRepository.findAll().stream()
                .filter(booking -> {
                    LocalDateTime startTime = booking.getStartTime();
                    return "confirmed".equalsIgnoreCase(booking.getStatus()) &&
                           startTime != null &&
                           startTime.isAfter(reminderStart) && startTime.isBefore(reminderEnd);
                })
                .toList();
            
            if (upcomingBookings.isEmpty()) {
                System.out.println("ℹ️  [ReminderService] No bookings found for 24-hour reminders");
                return;
            }
            
            System.out.println("📧 [ReminderService] Found " + upcomingBookings.size() + " bookings for reminders");
            
            // Send reminders
            for (Booking booking : upcomingBookings) {
                sendReminderEmail(booking);
            }
            
            System.out.println("✅ [ReminderService] Sent " + upcomingBookings.size() + " reminder(s)");
            
        } catch (Exception e) {
            System.err.println("❌ [ReminderService] Error sending reminders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendReminderEmail(Booking booking) {
        try {
            if (booking.getCustomerEmail() == null || booking.getCustomerEmail().trim().isEmpty()) {
                System.err.println("⚠️  [ReminderService] No email for booking #" + booking.getId());
                return;
            }

            String customerName = booking.getCustomerName() != null ? booking.getCustomerName() : "Valued Customer";
            String resourceName = booking.getResource() != null ? booking.getResource().getName() : "your service";
            String startTime = booking.getStartTime() != null ? 
                booking.getStartTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")) : "N/A";
            
            String subject = "🔔 Reminder: Your appointment is tomorrow!";
            String htmlContent = buildReminderEmailHtml(customerName, resourceName, startTime, booking.getId().toString());
            
            emailService.sendHtmlEmail(booking.getCustomerEmail(), subject, htmlContent);
            
            System.out.println("✅ [ReminderService] Sent email reminder for booking #" + booking.getId() + 
                             " to " + booking.getCustomerEmail());
            
        } catch (Exception e) {
            System.err.println("❌ [ReminderService] Failed to send email for booking #" + 
                             booking.getId() + ": " + e.getMessage());
        }
    }

    private String buildReminderEmailHtml(String customerName, String resourceName, String startTime, String bookingId) {
        return "<!DOCTYPE html>" +
                "<html><head><style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f7f6; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 20px auto; background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #26a69a 0%, #00897b 100%); color: #fff; padding: 30px; text-align: center; border-radius: 8px 8px 0 0; }" +
                ".content { padding: 30px; color: #555; }" +
                ".reminder-box { background: #fff9c4; border-left: 4px solid #fbc02d; padding: 15px; margin: 20px 0; border-radius: 4px; }" +
                ".booking-details { background: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0; }" +
                ".booking-details h3 { color: #00897b; margin-top: 0; }" +
                ".button { display: inline-block; background-color: #26a69a; color: #fff; padding: 12px 25px; border-radius: 5px; text-decoration: none; margin-top: 20px; }" +
                ".footer { background: #f0f0f0; padding: 20px; text-align: center; font-size: 12px; color: #777; }" +
                "</style></head><body>" +
                "<div class='container'>" +
                "<div class='header'><h1>🔔 Appointment Reminder</h1></div>" +
                "<div class='content'>" +
                "<p>Hi " + customerName + ",</p>" +
                "<div class='reminder-box'><strong>⏰ Reminder:</strong> Your appointment is in approximately 24 hours!</div>" +
                "<div class='booking-details'>" +
                "<h3>📋 Appointment Details</h3>" +
                "<p><strong>Booking ID:</strong> #" + bookingId + "</p>" +
                "<p><strong>Service:</strong> " + resourceName + "</p>" +
                "<p><strong>Date & Time:</strong> " + startTime + "</p>" +
                "</div>" +
                "<p>We look forward to seeing you!<br>The BookFast Team</p>" +
                "</div>" +
                "<div class='footer'><p>© 2025 BookFast. All rights reserved.</p></div>" +
                "</div></body></html>";
    }
}

