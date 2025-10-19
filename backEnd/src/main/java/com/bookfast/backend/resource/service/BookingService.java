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
        return repository.save(booking);
    }

    public List<Booking> getBookingsByResource(Long resourceId) {
        return repository.findByResourceId(resourceId);
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId);
    }
}