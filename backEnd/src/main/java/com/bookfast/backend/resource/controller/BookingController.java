package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;

import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }


    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return service.createBooking(booking);
    }

    @GetMapping("/customer/{customerId}")
    public List<Booking> getBookingsByCustomer(@PathVariable Long customerId) {
        return service.getBookingsByCustomer(customerId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<Booking> getBookingsByResource(@PathVariable Long resourceId) {
        return service.getBookingsByResource(resourceId);
    }
}