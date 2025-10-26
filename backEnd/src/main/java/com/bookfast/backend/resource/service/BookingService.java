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

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository repository, PaymentRepository paymentRepository, UserRepository userRepository) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public Booking createBooking(Booking booking) {
        // Prevent double booking: check for overlapping bookings
        List<Booking> overlaps = repository.findOverlappingBookings(
                booking.getResource().getId(),
                booking.getStartTime(),
                booking.getEndTime());
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Double booking detected: overlapping appointment exists.");
        }
        return repository.save(booking);
    }

    public List<Booking> getBookingsByResource(Long resourceId) {
        return repository.findByResource_Id(resourceId);
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        // First, delete all associated payments
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        for (Payment payment : payments) {
            paymentRepository.delete(payment);
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
}