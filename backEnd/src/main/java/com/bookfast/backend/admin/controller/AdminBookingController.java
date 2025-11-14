package com.bookfast.backend.admin.controller;

import com.bookfast.backend.common.dto.ErrorResponse;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bookings")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class AdminBookingController {
    private final BookingRepository bookingRepository;

    public AdminBookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllBookings() {
        try {
            List<Booking> bookings = bookingRepository.findAll();
            return ResponseEntity.ok(bookings);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch bookings: " + ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch booking: " + ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Booking bookingData) {
        try {
            return bookingRepository.findById(id)
                .map(booking -> {
                    if (bookingData.getStatus() != null) {
                        booking.setStatus(bookingData.getStatus());
                    }
                    if (bookingData.getPaymentStatus() != null) {
                        booking.setPaymentStatus(bookingData.getPaymentStatus());
                    }
                    if (bookingData.getFinalAmount() != null) {
                        booking.setFinalAmount(bookingData.getFinalAmount());
                    }
                    return ResponseEntity.ok(bookingRepository.save(booking));
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update booking: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            if (bookingRepository.existsById(id)) {
                bookingRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete booking: " + ex.getMessage()));
        }
    }
}

