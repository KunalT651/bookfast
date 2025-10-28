package com.bookfast.backend.resource.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.model.Payment;
import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.repository.PaymentRepository;
import com.bookfast.backend.resource.dto.BookingDetailsDTO;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.provider.service.GoogleCalendarService;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final GoogleCalendarService googleCalendarService;

    public BookingService(BookingRepository repository, PaymentRepository paymentRepository, UserRepository userRepository, GoogleCalendarService googleCalendarService) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.googleCalendarService = googleCalendarService;
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
        
        // Create Google Calendar event for the provider
        try {
            createGoogleCalendarEvent(savedBooking);
        } catch (Exception e) {
            // Log the error but don't fail the booking creation
            System.err.println("Failed to create Google Calendar event: " + e.getMessage());
        }
        
        return savedBooking;
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
            // Get provider information
            if (booking.getResource() != null && booking.getResource().getProviderId() != null) {
                User provider = userRepository.findById(booking.getResource().getProviderId()).orElse(null);
                if (provider != null) {
                    // Create event title
                    String eventTitle = String.format("BookFast: %s - %s", 
                        booking.getResource().getName(), 
                        booking.getCustomerName());
                    
                    // Format times
                    String startTime = booking.getStartTime().toString();
                    String endTime = booking.getEndTime().toString();
                    
                    // Create the calendar event
                    googleCalendarService.createCalendarEvent(
                        "demo_access_token", // In real implementation, get from provider's stored tokens
                        eventTitle,
                        startTime,
                        endTime
                    );
                    
                    System.out.println("Google Calendar event created for booking: " + booking.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating Google Calendar event: " + e.getMessage());
        }
    }

    private void updateGoogleCalendarEvent(Booking booking) {
        try {
            if (booking.getResource() != null && booking.getResource().getProviderId() != null) {
                User provider = userRepository.findById(booking.getResource().getProviderId()).orElse(null);
                if (provider != null) {
                    String eventTitle = String.format("BookFast: %s - %s", 
                        booking.getResource().getName(), 
                        booking.getCustomerName());
                    
                    String startTime = booking.getStartTime().toString();
                    String endTime = booking.getEndTime().toString();
                    
                    // In a real implementation, you would use the actual event ID from the database
                    String eventId = "booking_" + booking.getId();
                    
                    googleCalendarService.updateCalendarEvent(
                        eventId,
                        eventTitle,
                        startTime,
                        endTime
                    );
                    
                    System.out.println("Google Calendar event updated for booking: " + booking.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating Google Calendar event: " + e.getMessage());
        }
    }

    private void deleteGoogleCalendarEvent(Booking booking) {
        try {
            if (booking.getResource() != null && booking.getResource().getProviderId() != null) {
                // In a real implementation, you would use the actual event ID from the database
                String eventId = "booking_" + booking.getId();
                
                googleCalendarService.deleteCalendarEvent(eventId);
                
                System.out.println("Google Calendar event deleted for booking: " + booking.getId());
            }
        } catch (Exception e) {
            System.err.println("Error deleting Google Calendar event: " + e.getMessage());
        }
    }
}