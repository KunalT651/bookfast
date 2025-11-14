package com.bookfast.backend.admin.controller;

import com.bookfast.backend.common.dto.ErrorResponse;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/bookings")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class AdminBookingController {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public AdminBookingController(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllBookings() {
        try {
            List<Booking> bookings = bookingRepository.findAll();
            List<Map<String, Object>> bookingDTOs = new ArrayList<>();
            
            for (Booking booking : bookings) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", booking.getId());
                dto.put("customerId", booking.getCustomerId());
                dto.put("customerName", booking.getCustomerName());
                dto.put("customerEmail", booking.getCustomerEmail());
                dto.put("customerPhone", booking.getCustomerPhone());
                dto.put("customerZip", booking.getCustomerZip());
                dto.put("status", booking.getStatus());
                dto.put("paymentStatus", booking.getPaymentStatus());
                dto.put("finalAmount", booking.getFinalAmount());
                dto.put("startTime", booking.getStartTime());
                dto.put("endTime", booking.getEndTime());
                dto.put("date", booking.getDate());
                dto.put("startTimeStr", booking.getStartTimeStr());
                dto.put("endTimeStr", booking.getEndTimeStr());
                dto.put("slotId", booking.getSlotId());
                
                // Populate resource information
                if (booking.getResource() != null) {
                    dto.put("resourceId", booking.getResource().getId());
                    dto.put("resourceName", booking.getResource().getName());
                    dto.put("resourceDescription", booking.getResource().getDescription());
                    dto.put("resourcePrice", booking.getResource().getPrice());
                    
                    // Populate provider information
                    if (booking.getResource().getProviderId() != null) {
                        Optional<User> providerOpt = userRepository.findById(booking.getResource().getProviderId());
                        if (providerOpt.isPresent()) {
                            User provider = providerOpt.get();
                            dto.put("providerId", provider.getId());
                            dto.put("providerName", provider.getFirstName() + " " + provider.getLastName());
                            dto.put("providerEmail", provider.getEmail());
                            dto.put("providerOrganization", provider.getOrganizationName());
                        } else {
                            dto.put("providerName", "N/A");
                        }
                    } else {
                        dto.put("providerName", "N/A");
                    }
                } else {
                    dto.put("resourceName", "N/A");
                    dto.put("providerName", "N/A");
                }
                
                bookingDTOs.add(dto);
            }
            
            return ResponseEntity.ok(bookingDTOs);
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

