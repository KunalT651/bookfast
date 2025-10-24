package com.bookfast.backend.resource.service;

import org.springframework.stereotype.Service;

import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.repository.BookingRepository;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
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

    public void deleteBooking(Long bookingId) {
        repository.deleteById(bookingId);
    }

    public Booking cancelBooking(Long bookingId) {
        Booking booking = repository.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatus("cancelled");
            return repository.save(booking);
        }
        return null;
    }

    public Booking getBookingById(Long bookingId) {
        return repository.findById(bookingId).orElse(null);
    }

    public Booking saveBooking(Booking booking) {
        return repository.save(booking);
    }
}