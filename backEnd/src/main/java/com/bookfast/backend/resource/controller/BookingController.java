package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.resource.service.BookingService;
import com.bookfast.backend.resource.dto.BookingDetailsDTO;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {
    private final BookingService service;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public BookingController(BookingService service, ResourceRepository resourceRepository, UserRepository userRepository) {
        this.service = service;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
        System.out.println("[BookingController] Received booking request:");
        System.out.println("  resourceId: " + (booking.getResource() != null ? booking.getResource().getId() : "null"));
        System.out.println("  customerName: " + booking.getCustomerName());
        System.out.println("  customerEmail: " + booking.getCustomerEmail());
        System.out.println("  date: " + booking.getDate());
        System.out.println("  startTimeStr: " + booking.getStartTimeStr());
        System.out.println("  endTimeStr: " + booking.getEndTimeStr());
        
        // Set customerId from JWT authentication
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("[BookingController] Authentication: " + (authentication != null ? authentication.getClass().getSimpleName() : "null"));
            System.out.println("[BookingController] Principal: " + (authentication != null ? authentication.getPrincipal().getClass().getSimpleName() : "null"));
            
            if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                org.springframework.security.core.userdetails.UserDetails userDetails = 
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();
                System.out.println("[BookingController] Username from JWT: " + username);
                
                User user = userRepository.findByEmail(username).orElse(null);
                if (user != null) {
                    booking.setCustomerId(user.getId());
                    System.out.println("[BookingController] Set customerId from JWT: " + user.getId());
                } else {
                    System.out.println("[BookingController] User not found in database for email: " + username);
                }
            } else {
                System.out.println("[BookingController] Authentication or Principal is not UserDetails");
            }
        } catch (Exception e) {
            System.out.println("[BookingController] Error setting customerId: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Fallback: If customerId is still null, try to find user by email
        if (booking.getCustomerId() == null && booking.getCustomerEmail() != null) {
            try {
                User user = userRepository.findByEmail(booking.getCustomerEmail()).orElse(null);
                if (user != null) {
                    booking.setCustomerId(user.getId());
                    System.out.println("[BookingController] Set customerId from email fallback: " + user.getId());
                } else {
                    System.out.println("[BookingController] No user found for email: " + booking.getCustomerEmail());
                }
            } catch (Exception e) {
                System.out.println("[BookingController] Error in email fallback: " + e.getMessage());
            }
        }
        
        // Always set Resource from resourceId (if Booking exposes a resourceId field)
        if (booking.getResource() == null) {
            try {
                java.lang.reflect.Field resourceIdField = booking.getClass().getDeclaredField("resourceId");
                resourceIdField.setAccessible(true);
                Object resourceIdObj = resourceIdField.get(booking);
                if (resourceIdObj != null) {
                    Long resourceId = Long.valueOf(resourceIdObj.toString());
                    System.out.println("[BookingController] Looking up resource with id: " + resourceId);
                    Resource resource = resourceRepository.findById(resourceId).orElse(null);
                    if (resource == null) {
                        System.out.println("[BookingController] Resource not found for id: " + resourceId);
                        throw new IllegalArgumentException("Resource not found for id: " + resourceId);
                    }
                    booking.setResource(resource);
                    System.out.println("[BookingController] Resource set successfully: " + resource.getName());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.out.println("[BookingController] Error accessing resourceId field: " + e.getMessage());
            }
        }
        // Convert startTime/endTime from string if needed
        try {
            if (booking.getStartTime() == null && booking.getDate() != null && booking.getStartTimeStr() != null) {
                String dateTimeStr = booking.getDate() + "T" + booking.getStartTimeStr();
                System.out.println("[BookingController] Parsing startTime: " + dateTimeStr);
                booking.setStartTime(java.time.LocalDateTime.parse(dateTimeStr));
            }
            if (booking.getEndTime() == null && booking.getDate() != null && booking.getEndTimeStr() != null) {
                String dateTimeStr = booking.getDate() + "T" + booking.getEndTimeStr();
                System.out.println("[BookingController] Parsing endTime: " + dateTimeStr);
                booking.setEndTime(java.time.LocalDateTime.parse(dateTimeStr));
            }
        } catch (Exception e) {
            System.out.println("[BookingController] Error parsing date/time: " + e.getMessage());
            throw new IllegalArgumentException("Invalid date/time format: " + e.getMessage());
        }
        
        // Validate required fields
        if (booking.getCustomerName() == null || booking.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (booking.getCustomerEmail() == null || booking.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }
        if (booking.getResource() == null) {
            throw new IllegalArgumentException("Resource is required");
        }
        
        System.out.println("[BookingController] Creating booking for customer: " + booking.getCustomerName());
        Booking saved = service.createBooking(booking);
        System.out.println("[BookingController] Booking created successfully with id: " + saved.getId());
        return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.out.println("[BookingController] ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable Long bookingId, @RequestBody Booking booking) {
        try {
            System.out.println("[BookingController] Updating booking with id: " + bookingId);
            
            // Set resource from resourceId if needed
            if (booking.getResource() == null) {
                try {
                    java.lang.reflect.Field resourceIdField = booking.getClass().getDeclaredField("resourceId");
                    resourceIdField.setAccessible(true);
                    Object resourceIdObj = resourceIdField.get(booking);
                    if (resourceIdObj != null) {
                        Long resourceId = Long.valueOf(resourceIdObj.toString());
                        Resource resource = resourceRepository.findById(resourceId).orElse(null);
                        if (resource == null) {
                            throw new IllegalArgumentException("Resource not found for id: " + resourceId);
                        }
                        booking.setResource(resource);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("[BookingController] Error accessing resourceId field: " + e.getMessage());
                }
            }
            
            // Convert startTime/endTime from string if needed
            if (booking.getStartTime() == null && booking.getStartTimeStr() != null) {
                booking.setStartTime(java.time.LocalDateTime.parse(booking.getStartTimeStr()));
            }
            if (booking.getEndTime() == null && booking.getEndTimeStr() != null) {
                booking.setEndTime(java.time.LocalDateTime.parse(booking.getEndTimeStr()));
            }
            
            Booking updated = service.updateBooking(bookingId, booking);
            System.out.println("[BookingController] Booking updated successfully with id: " + updated.getId());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            System.out.println("[BookingController] ERROR updating booking: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/multi")
    public List<Booking> createMultiSlotBooking(@RequestBody Map<String, Object> payload) {
        // Demo/academic mode: No authentication or role checks
        Long resourceId = Long.valueOf(payload.get("resourceId").toString());
        List<?> slotIdsRaw = (List<?>) payload.get("slotIds");
        List<Integer> slotIds = new ArrayList<>();
        for (Object o : slotIdsRaw) {
            slotIds.add(Integer.valueOf(o.toString()));
        }
        String customerName = (String) payload.get("customerName");
        String customerEmail = (String) payload.get("customerEmail");
        String customerPhone = (String) payload.get("customerPhone");
        String customerZip = (String) payload.getOrDefault("customerZip", "");
        Double finalAmount = payload.get("finalAmount") != null ? Double.valueOf(payload.get("finalAmount").toString())
                : null;
        String status = (String) payload.get("status");
        String paymentStatus = (String) payload.get("paymentStatus");
        List<Booking> bookings = new ArrayList<>();
        for (Integer slotId : slotIds) {
            Booking booking = new Booking();
            booking.setCustomerName(customerName);
            booking.setCustomerEmail(customerEmail);
            booking.setCustomerPhone(customerPhone);
            booking.setCustomerZip(customerZip);
            booking.setStatus(status);
            booking.setPaymentStatus(paymentStatus);
            booking.setSlotId(slotId);
            booking.setFinalAmount(finalAmount);
            // Optionally set resourceId if model supports direct field
            bookings.add(service.createBooking(booking));
        }
        System.out.println("[BookingController] Created multi-slot bookings for customer: " + customerName);
        return bookings;
    }

    @GetMapping("/customer/{customerId}")
    public List<Booking> getBookingsByCustomer(@PathVariable Long customerId) {
        return service.getBookingsByCustomer(customerId);
    }

    @GetMapping("/resource/{resourceId}")
    public List<Booking> getBookingsByResource(@PathVariable Long resourceId) {
        return service.getBookingsByResource(resourceId);
    }

    @GetMapping("/provider/me")
    public List<BookingDetailsDTO> getBookingsByProvider() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String username = authentication.getName(); // This is the email
                User currentUser = userRepository.findByEmail(username).orElse(null);
                if (currentUser != null) {
                    return service.getDetailedBookingsByProvider(currentUser.getId());
                }
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("[BookingController] Error getting provider bookings: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        System.out.println("[BookingController] DELETE request received for booking: " + bookingId);
        
        // Check authentication context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("[BookingController] Authentication: " + auth.getName());
            System.out.println("[BookingController] Authorities: " + auth.getAuthorities());
        } else {
            System.out.println("[BookingController] No authentication found!");
        }
        
        System.out.println("[BookingController] Deleting booking: " + bookingId);
        service.deleteBooking(bookingId);
        System.out.println("[BookingController] Booking deleted successfully: " + bookingId);
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            service.cancelBooking(bookingId);
            return ResponseEntity.ok().body(Map.of("message", "Booking cancelled and deleted successfully"));
        } catch (Exception e) {
            System.out.println("[BookingController] Error cancelling booking: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to cancel booking: " + e.getMessage()));
        }
    }

    @PutMapping("/provider/{providerId}/edit/{bookingId}")
    public Booking providerEditBooking(@PathVariable Long providerId, @PathVariable Long bookingId,
            @RequestBody Booking updated) {
        System.out.println("[BookingController] Provider edit booking - providerId: " + providerId + ", bookingId: " + bookingId);
        System.out.println("[BookingController] Updated data: " + updated);
        
        // Only allow status and time changes, not customer info
        Booking booking = service.getBookingById(bookingId);
        if (booking != null && booking.getResource() != null
                && booking.getResource().getProviderId().equals(providerId)) {
            System.out.println("[BookingController] Found booking, updating...");
            booking.setStatus(updated.getStatus());
            booking.setStartTime(updated.getStartTime());
            booking.setEndTime(updated.getEndTime());
            Booking saved = service.saveBooking(booking);
            System.out.println("[BookingController] Booking updated successfully: " + saved);
            return saved;
        }
        System.out.println("[BookingController] Booking not found or provider mismatch");
        return null;
    }

    @PutMapping("/provider/{providerId}/cancel/{bookingId}")
    public Booking providerCancelBooking(@PathVariable Long providerId, @PathVariable Long bookingId) {
        System.out.println("[BookingController] Provider cancel booking - providerId: " + providerId + ", bookingId: " + bookingId);
        
        Booking booking = service.getBookingById(bookingId);
        if (booking != null && booking.getResource() != null
                && booking.getResource().getProviderId().equals(providerId)) {
            System.out.println("[BookingController] Found booking, cancelling...");
            booking.setStatus("cancelled");
            Booking saved = service.saveBooking(booking);
            System.out.println("[BookingController] Booking cancelled successfully: " + saved);
            return saved;
        }
        System.out.println("[BookingController] Booking not found or provider mismatch");
        return null;
    }
}