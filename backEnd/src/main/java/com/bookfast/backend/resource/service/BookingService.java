package com.bookfast.backend.resource.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.model.Payment;
import com.bookfast.backend.resource.model.AvailabilitySlot;
import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.repository.PaymentRepository;
import com.bookfast.backend.resource.repository.AvailabilitySlotRepository;
import com.bookfast.backend.resource.dto.BookingDetailsDTO;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.provider.service.GoogleCalendarService;
import com.bookfast.backend.common.notification.EmailService;
import com.bookfast.backend.common.notification.SmsService;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final GoogleCalendarService googleCalendarService;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    public BookingService(BookingRepository repository, PaymentRepository paymentRepository, 
                         UserRepository userRepository, GoogleCalendarService googleCalendarService,
                         AvailabilitySlotRepository availabilitySlotRepository,
                         EmailService emailService, SmsService smsService) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.googleCalendarService = googleCalendarService;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    public Booking createBooking(Booking booking) {
        // Validate required fields
        if (booking.getResource() == null || booking.getResource().getId() == null) {
            throw new IllegalArgumentException("Resource is required for booking");
        }
        if (booking.getStartTime() == null || booking.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time are required for booking");
        }
        if (booking.getStartTime().isAfter(booking.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        
        // Prevent double booking: check for overlapping bookings
        List<Booking> overlaps = repository.findOverlappingBookings(
                booking.getResource().getId(),
                booking.getStartTime(),
                booking.getEndTime());
        
        if (!overlaps.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Double booking detected! This time slot is already booked. ");
            errorMessage.append("Conflicting booking(s): ");
            for (int i = 0; i < overlaps.size(); i++) {
                Booking conflict = overlaps.get(i);
                errorMessage.append(String.format("Booking #%d (Customer: %s, Time: %s to %s)", 
                    conflict.getId(), 
                    conflict.getCustomerName(),
                    conflict.getStartTime(),
                    conflict.getEndTime()));
                if (i < overlaps.size() - 1) {
                    errorMessage.append(", ");
                }
            }
            throw new IllegalStateException(errorMessage.toString());
        }
        
        // Save the booking first
        Booking savedBooking = repository.save(booking);
        
        // Mark the slot as booked
        if (savedBooking.getSlotId() != null) {
            try {
                Optional<AvailabilitySlot> slotOpt = availabilitySlotRepository.findById(Long.valueOf(savedBooking.getSlotId()));
                if (slotOpt.isPresent()) {
                    AvailabilitySlot slot = slotOpt.get();
                    slot.setStatus("booked");
                    availabilitySlotRepository.save(slot);
                    System.out.println("[BookingService] Marked slot " + savedBooking.getSlotId() + " as booked");
                }
            } catch (Exception e) {
                System.err.println("Failed to update slot status: " + e.getMessage());
            }
        }
        
        // Create Google Calendar event for the provider
        try {
            createGoogleCalendarEvent(savedBooking);
        } catch (Exception e) {
            // Log the error but don't fail the booking creation
            System.err.println("Failed to create Google Calendar event: " + e.getMessage());
        }
        
        // Send confirmation notifications (email & SMS)
        try {
            sendBookingConfirmationNotifications(savedBooking);
        } catch (Exception e) {
            // Log the error but don't fail the booking creation
            System.err.println("[BookingService] Failed to send notifications: " + e.getMessage());
        }
        
        return savedBooking;
    }
    
    /**
     * Send booking confirmation notifications via email and SMS
     */
    private void sendBookingConfirmationNotifications(Booking booking) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        
        String date = booking.getStartTime().format(dateFormatter);
        String startTime = booking.getStartTime().format(timeFormatter);
        String endTime = booking.getEndTime().format(timeFormatter);
        
        String serviceName = booking.getResource() != null ? booking.getResource().getName() : "Service";
        
        // Get provider name from User entity
        String providerName = "Provider";
        if (booking.getResource() != null && booking.getResource().getProviderId() != null) {
            Optional<User> providerOpt = userRepository.findById(booking.getResource().getProviderId());
            if (providerOpt.isPresent()) {
                User provider = providerOpt.get();
                providerName = provider.getFirstName() + " " + provider.getLastName();
            }
        }
        
        // Send Email Confirmation
        if (booking.getCustomerEmail() != null && !booking.getCustomerEmail().isEmpty()) {
            try {
                String emailContent = createBookingConfirmationEmail(booking, date, startTime, endTime, serviceName, providerName);
                emailService.sendHtmlEmail(
                    booking.getCustomerEmail(),
                    "Booking Confirmation - " + serviceName,
                    emailContent
                );
                System.out.println("[BookingService] Confirmation email sent to: " + booking.getCustomerEmail());
            } catch (Exception e) {
                System.err.println("[BookingService] Failed to send confirmation email: " + e.getMessage());
            }
        }
        
        // Send SMS Confirmation (only if phone number is provided)
        if (booking.getCustomerPhone() != null && !booking.getCustomerPhone().trim().isEmpty()) {
            try {
                String smsContent = createBookingConfirmationSms(booking, date, startTime, endTime, serviceName, providerName);
                boolean smsSent = smsService.sendSms(booking.getCustomerPhone(), smsContent);
                if (smsSent) {
                    System.out.println("[BookingService] Confirmation SMS sent to: " + booking.getCustomerPhone());
                } else {
                    System.out.println("[BookingService] SMS not sent (Twilio not configured or phone number invalid)");
                }
            } catch (Exception e) {
                System.err.println("[BookingService] Failed to send confirmation SMS: " + e.getMessage());
            }
        } else {
            System.out.println("[BookingService] No phone number provided. Skipping SMS notification.");
        }
    }
    
    /**
     * Create HTML email content for booking confirmation
     */
    private String createBookingConfirmationEmail(Booking booking, String date, String startTime, 
                                                  String endTime, String serviceName, String providerName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #10b981; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
                    .booking-details { background-color: white; padding: 20px; margin: 20px 0; border-left: 4px solid #10b981; border-radius: 4px; }
                    .detail-item { margin: 12px 0; }
                    .detail-label { font-weight: bold; color: #10b981; }
                    .detail-value { margin-left: 10px; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 0.875rem; }
                    .important { background-color: #fef3c7; padding: 15px; border-radius: 6px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Booking Confirmed!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Your booking has been successfully confirmed! Here are the details:</p>
                        
                        <div class="booking-details">
                            <h3>📋 Booking Details:</h3>
                            <div class="detail-item">
                                <span class="detail-label">Booking ID:</span>
                                <span class="detail-value">#%d</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Service:</span>
                                <span class="detail-value">%s</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Provider:</span>
                                <span class="detail-value">%s</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Date:</span>
                                <span class="detail-value">%s</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Time:</span>
                                <span class="detail-value">%s - %s</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Amount:</span>
                                <span class="detail-value">$%.2f</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Status:</span>
                                <span class="detail-value">%s</span>
                            </div>
                        </div>
                        
                        <div class="important">
                            <strong>⏰ Reminder:</strong> Please arrive 5 minutes before your scheduled time.
                        </div>
                        
                        <p>If you need to cancel or reschedule, please log in to your account or contact us at least 24 hours in advance.</p>
                        
                        <p>Thank you for choosing BookFast!</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 BookFast. All rights reserved.</p>
                        <p>This is an automated message. Please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            booking.getCustomerName(),
            booking.getId(),
            serviceName,
            providerName,
            date,
            startTime,
            endTime,
            booking.getFinalAmount() != null ? booking.getFinalAmount() : 0.0,
            booking.getStatus() != null ? booking.getStatus().toUpperCase() : "PENDING"
        );
    }
    
    /**
     * Create SMS content for booking confirmation
     */
    private String createBookingConfirmationSms(Booking booking, String date, String startTime, 
                                                String endTime, String serviceName, String providerName) {
        return String.format(
            "✅ BookFast Booking Confirmed!\n\n" +
            "Booking #%d\n" +
            "Service: %s\n" +
            "Provider: %s\n" +
            "Date: %s\n" +
            "Time: %s - %s\n" +
            "Amount: $%.2f\n\n" +
            "Please arrive 5 min early. Thank you!",
            booking.getId(),
            serviceName,
            providerName,
            date,
            startTime,
            endTime,
            booking.getFinalAmount() != null ? booking.getFinalAmount() : 0.0
        );
    }

    public List<Booking> getBookingsByResource(Long resourceId) {
        return repository.findByResource_Id(resourceId);
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    public Booking updateBooking(Long bookingId, Booking updatedBooking) {
        // Find existing booking
        Booking existingBooking = repository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));
        
        // Validate required fields
        if (updatedBooking.getResource() == null || updatedBooking.getResource().getId() == null) {
            throw new IllegalArgumentException("Resource is required for booking");
        }
        if (updatedBooking.getStartTime() == null || updatedBooking.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time are required for booking");
        }
        if (updatedBooking.getStartTime().isAfter(updatedBooking.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        
        // Prevent double booking: check for overlapping bookings (excluding current booking)
        List<Booking> overlaps = repository.findOverlappingBookings(
                updatedBooking.getResource().getId(),
                updatedBooking.getStartTime(),
                updatedBooking.getEndTime());
        
        // Filter out the current booking from overlaps
        overlaps = overlaps.stream()
            .filter(booking -> !booking.getId().equals(bookingId))
            .collect(java.util.stream.Collectors.toList());
        
        if (!overlaps.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Double booking detected! This time slot is already booked. ");
            errorMessage.append("Conflicting booking(s): ");
            for (int i = 0; i < overlaps.size(); i++) {
                Booking conflict = overlaps.get(i);
                errorMessage.append(String.format("Booking #%d (Customer: %s, Time: %s to %s)", 
                    conflict.getId(), 
                    conflict.getCustomerName(),
                    conflict.getStartTime(),
                    conflict.getEndTime()));
                if (i < overlaps.size() - 1) {
                    errorMessage.append(", ");
                }
            }
            throw new IllegalStateException(errorMessage.toString());
        }
        
        // Update the booking fields
        existingBooking.setCustomerName(updatedBooking.getCustomerName());
        existingBooking.setCustomerEmail(updatedBooking.getCustomerEmail());
        existingBooking.setCustomerPhone(updatedBooking.getCustomerPhone());
        existingBooking.setCustomerZip(updatedBooking.getCustomerZip());
        existingBooking.setStartTime(updatedBooking.getStartTime());
        existingBooking.setEndTime(updatedBooking.getEndTime());
        existingBooking.setDate(updatedBooking.getDate());
        existingBooking.setStatus(updatedBooking.getStatus());
        existingBooking.setFinalAmount(updatedBooking.getFinalAmount());
        
        Booking savedBooking = repository.save(existingBooking);
        
        // Update Google Calendar event
        try {
            updateGoogleCalendarEvent(savedBooking);
        } catch (Exception e) {
            System.err.println("Failed to update Google Calendar event: " + e.getMessage());
        }
        
        return savedBooking;
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        // Get booking details before deletion for Google Calendar cleanup
        Booking booking = repository.findById(bookingId).orElse(null);
        
        // First, delete all associated payments
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        for (Payment payment : payments) {
            paymentRepository.delete(payment);
        }
        
        // Delete Google Calendar event
        if (booking != null) {
            try {
                deleteGoogleCalendarEvent(booking);
            } catch (Exception e) {
                System.err.println("Failed to delete Google Calendar event: " + e.getMessage());
            }
        }
        
        // Then delete the booking
        repository.deleteById(bookingId);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        System.out.println("[BookingService] Attempting to cancel and delete booking with ID: " + bookingId);
        
        Booking booking = repository.findById(bookingId).orElse(null);
        if (booking == null) {
            System.out.println("[BookingService] Booking not found with ID: " + bookingId);
            throw new RuntimeException("Booking not found with ID: " + bookingId);
        }
        
        System.out.println("[BookingService] Found booking: " + booking.getId() + ", current status: " + booking.getStatus());
        
        // Store slotId before deleting booking
        Integer slotId = booking.getSlotId();
        
        try {
            // First, delete all associated payments
            System.out.println("[BookingService] Looking for payments for booking ID: " + bookingId);
            List<Payment> payments = paymentRepository.findByBookingId(bookingId);
            System.out.println("[BookingService] Found " + payments.size() + " payments to delete");
            
            for (Payment payment : payments) {
                System.out.println("[BookingService] Deleting payment ID: " + payment.getId());
                paymentRepository.delete(payment);
            }
            
            // Then delete the booking
            System.out.println("[BookingService] Deleting booking ID: " + bookingId);
            repository.deleteById(bookingId);
            
            // Mark the slot as available again
            if (slotId != null) {
                try {
                    Optional<AvailabilitySlot> slotOpt = availabilitySlotRepository.findById(Long.valueOf(slotId));
                    if (slotOpt.isPresent()) {
                        AvailabilitySlot slot = slotOpt.get();
                        slot.setStatus("available");
                        availabilitySlotRepository.save(slot);
                        System.out.println("[BookingService] Marked slot " + slotId + " as available again");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to update slot status: " + e.getMessage());
                }
            }
            
            System.out.println("[BookingService] Successfully cancelled and deleted booking ID: " + bookingId);
        } catch (Exception e) {
            System.out.println("[BookingService] Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Booking getBookingById(Long bookingId) {
        return repository.findById(bookingId).orElse(null);
    }

    public Booking saveBooking(Booking booking) {
        return repository.save(booking);
    }

    public List<Booking> getBookingsByProvider(Long providerId) {
        return repository.findByResourceProviderId(providerId);
    }

    public List<BookingDetailsDTO> getDetailedBookingsByProvider(Long providerId) {
        List<Booking> bookings = repository.findByResourceProviderId(providerId);
        List<BookingDetailsDTO> detailedBookings = new ArrayList<>();
        
        for (Booking booking : bookings) {
            BookingDetailsDTO dto = new BookingDetailsDTO();
            
            // Basic booking information
            dto.setId(booking.getId());
            dto.setCustomerId(booking.getCustomerId());
            dto.setCustomerName(booking.getCustomerName());
            dto.setCustomerEmail(booking.getCustomerEmail());
            dto.setCustomerPhone(booking.getCustomerPhone());
            dto.setCustomerZip(booking.getCustomerZip());
            dto.setStatus(booking.getStatus());
            dto.setPaymentStatus(booking.getPaymentStatus());
            dto.setFinalAmount(booking.getFinalAmount());
            dto.setStartTime(booking.getStartTime());
            dto.setEndTime(booking.getEndTime());
            dto.setDate(booking.getDate());
            dto.setStartTimeStr(booking.getStartTimeStr());
            dto.setEndTimeStr(booking.getEndTimeStr());
            
            // Resource information
            if (booking.getResource() != null) {
                dto.setResourceId(booking.getResource().getId());
                dto.setResourceName(booking.getResource().getName());
                dto.setResourceDescription(booking.getResource().getDescription());
                dto.setResourcePrice(booking.getResource().getPrice());
                dto.setResourceSpecialization(booking.getResource().getSpecialization());
                dto.setProviderId(booking.getResource().getProviderId());
                
                // Get provider details from User table
                if (booking.getResource().getProviderId() != null) {
                    Optional<User> provider = userRepository.findById(booking.getResource().getProviderId());
                    if (provider.isPresent()) {
                        User providerUser = provider.get();
                        dto.setProviderName(providerUser.getFirstName() + " " + providerUser.getLastName());
                        dto.setProviderEmail(providerUser.getEmail());
                        dto.setProviderOrganization(providerUser.getOrganizationName());
                        dto.setProviderServiceCategory(providerUser.getServiceCategory());
                    }
                }
            }
            
            detailedBookings.add(dto);
        }
        
        return detailedBookings;
    }

    private void createGoogleCalendarEvent(Booking booking) {
        try {
            System.out.println("=== CALENDAR EVENT CREATION DEBUG ===");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("Customer ID: " + booking.getCustomerId());
            System.out.println("Resource ID: " + (booking.getResource() != null ? booking.getResource().getId() : "null"));
            System.out.println("Provider ID: " + (booking.getResource() != null ? booking.getResource().getProviderId() : "null"));
            
            // Get provider information
            if (booking.getResource() != null && booking.getResource().getProviderId() != null) {
                User provider = userRepository.findById(booking.getResource().getProviderId()).orElse(null);
                if (provider != null) {
                    System.out.println("Provider found: " + provider.getEmail());
                    System.out.println("Provider calendar connected: " + googleCalendarService.isCalendarConnected(provider.getId()));
                    
                    if (googleCalendarService.isCalendarConnected(provider.getId())) {
                        // Create event title
                        String eventTitle = String.format("BookFast: %s - %s", 
                            booking.getResource().getName(), 
                            booking.getCustomerName());
                        
                        // Create the calendar event for provider
                        String eventId = googleCalendarService.createCalendarEvent(
                            provider.getId(),
                            eventTitle,
                            booking.getStartTime(),
                            booking.getEndTime()
                        );
                        
                        System.out.println("✅ Google Calendar event created for provider booking: " + booking.getId() + ", Event ID: " + eventId);
                    } else {
                        System.out.println("❌ Provider calendar not connected - skipping calendar event creation");
                    }
                } else {
                    System.out.println("❌ Provider not found");
                }
            }

            // Also create calendar event for customer if they have calendar connected
            if (booking.getCustomerId() != null) {
                User customer = userRepository.findById(booking.getCustomerId()).orElse(null);
                if (customer != null) {
                    System.out.println("Customer found: " + customer.getEmail());
                    System.out.println("Customer calendar connected: " + googleCalendarService.isCalendarConnected(customer.getId()));
                    
                    if (googleCalendarService.isCalendarConnected(customer.getId())) {
                        String eventTitle = String.format("BookFast Booking: %s", 
                            booking.getResource().getName());
                        
                        String eventId = googleCalendarService.createCalendarEvent(
                            customer.getId(),
                            eventTitle,
                            booking.getStartTime(),
                            booking.getEndTime()
                        );
                        
                        System.out.println("✅ Google Calendar event created for customer booking: " + booking.getId() + ", Event ID: " + eventId);
                    } else {
                        System.out.println("❌ Customer calendar not connected - skipping calendar event creation");
                    }
                } else {
                    System.out.println("❌ Customer not found");
                }
            }
            System.out.println("=== END CALENDAR EVENT CREATION DEBUG ===");
        } catch (Exception e) {
            System.err.println("❌ Error creating Google Calendar event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateGoogleCalendarEvent(Booking booking) {
        try {
            if (booking.getResource() != null && booking.getResource().getProviderId() != null) {
                User provider = userRepository.findById(booking.getResource().getProviderId()).orElse(null);
                if (provider != null && googleCalendarService.isCalendarConnected(provider.getId())) {
                    String eventTitle = String.format("BookFast: %s - %s", 
                        booking.getResource().getName(), 
                        booking.getCustomerName());
                    
                    // In a real implementation, you would use the actual event ID from the database
                    String eventId = "booking_" + booking.getId();
                    
                    googleCalendarService.updateCalendarEvent(
                        provider.getId(),
                        eventId,
                        eventTitle,
                        booking.getStartTime(),
                        booking.getEndTime()
                    );
                    
                    System.out.println("Google Calendar event updated for booking: " + booking.getId());
                }
            }

            // Also update customer calendar event if connected
            if (booking.getCustomerId() != null) {
                User customer = userRepository.findById(booking.getCustomerId()).orElse(null);
                if (customer != null && googleCalendarService.isCalendarConnected(customer.getId())) {
                    String eventTitle = String.format("BookFast Booking: %s", 
                        booking.getResource().getName());
                    
                    String eventId = "customer_booking_" + booking.getId();
                    
                    googleCalendarService.updateCalendarEvent(
                        customer.getId(),
                        eventId,
                        eventTitle,
                        booking.getStartTime(),
                        booking.getEndTime()
                    );
                    
                    System.out.println("Customer Google Calendar event updated for booking: " + booking.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating Google Calendar event: " + e.getMessage());
        }
    }

    private void deleteGoogleCalendarEvent(Booking booking) {
        try {
            if (booking.getResource() != null && booking.getResource().getProviderId() != null) {
                User provider = userRepository.findById(booking.getResource().getProviderId()).orElse(null);
                if (provider != null && googleCalendarService.isCalendarConnected(provider.getId())) {
                    // In a real implementation, you would use the actual event ID from the database
                    String eventId = "booking_" + booking.getId();
                    
                    googleCalendarService.deleteCalendarEvent(provider.getId(), eventId);
                    
                    System.out.println("Google Calendar event deleted for booking: " + booking.getId());
                }
            }

            // Also delete customer calendar event if connected
            if (booking.getCustomerId() != null) {
                User customer = userRepository.findById(booking.getCustomerId()).orElse(null);
                if (customer != null && googleCalendarService.isCalendarConnected(customer.getId())) {
                    String eventId = "customer_booking_" + booking.getId();
                    
                    googleCalendarService.deleteCalendarEvent(customer.getId(), eventId);
                    
                    System.out.println("Customer Google Calendar event deleted for booking: " + booking.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting Google Calendar event: " + e.getMessage());
        }
    }
}