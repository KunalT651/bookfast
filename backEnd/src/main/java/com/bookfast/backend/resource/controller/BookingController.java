package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.service.BookingService;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class BookingController {
    private final BookingService service;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public BookingController(BookingService service, ResourceRepository resourceRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.service = service;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        // Set customerId from authenticated user
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String username = authentication.getName(); // This is the email
                User currentUser = userRepository.findByEmail(username).orElse(null);
                if (currentUser != null && currentUser.getId() != null) {
                    booking.setCustomerId(currentUser.getId());
                    System.out.println("[BookingController] Setting customerId from authenticated user: " + currentUser.getId());
                }
            }
        } catch (Exception e) {
            System.err.println("[BookingController] Error getting authenticated user: " + e.getMessage());
        }
        
        // Always set Resource from resourceId (if Booking exposes a resourceId field)
        if (booking.getResource() == null) {
            try {
                java.lang.reflect.Field resourceIdField = booking.getClass().getDeclaredField("resourceId");
                resourceIdField.setAccessible(true);
                Object resourceIdObj = resourceIdField.get(booking);
                if (resourceIdObj != null) {
                    Long resourceId = Long.valueOf(resourceIdObj.toString());
                    Resource resource = resourceRepository.findById(resourceId).orElse(null);
                    if (resource == null) {
                        System.out.println("[BookingController] Resource not found for id: " + resourceId);
                    }
                    booking.setResource(resource);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Booking does not have a resourceId field or it is inaccessible; nothing to
                // set
            }
        }
        // Convert startTime/endTime from string if needed
        if (booking.getStartTime() == null && booking.getDate() != null && booking.getStartTimeStr() != null) {
            booking.setStartTime(java.time.LocalDateTime.parse(booking.getDate() + "T" + booking.getStartTimeStr()));
        }
        if (booking.getEndTime() == null && booking.getDate() != null && booking.getEndTimeStr() != null) {
            booking.setEndTime(java.time.LocalDateTime.parse(booking.getDate() + "T" + booking.getEndTimeStr()));
        }
        System.out.println("[BookingController] Creating booking for customer ID: " + booking.getCustomerId() + ", name: " + booking.getCustomerName());
        Booking saved = service.createBooking(booking);
        return saved;
    }

    @PostMapping("/multi")
    public List<Booking> createMultiSlotBooking(@RequestBody Map<String, Object> payload) {
        // Get customerId from authenticated user
        Long customerId = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String username = authentication.getName(); // This is the email
                User currentUser = userRepository.findByEmail(username).orElse(null);
                if (currentUser != null && currentUser.getId() != null) {
                    customerId = currentUser.getId();
                    System.out.println("[BookingController] Setting customerId from authenticated user: " + customerId);
                }
            }
        } catch (Exception e) {
            System.err.println("[BookingController] Error getting authenticated user: " + e.getMessage());
        }
        
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
            booking.setCustomerId(customerId); // Set customerId from authenticated user
            booking.setCustomerName(customerName);
            booking.setCustomerEmail(customerEmail);
            booking.setCustomerPhone(customerPhone);
            booking.setCustomerZip(customerZip);
            booking.setStatus(status);
            booking.setPaymentStatus(paymentStatus);
            booking.setSlotId(slotId);
            booking.setFinalAmount(finalAmount);
            // Set resource
            Resource resource = resourceRepository.findById(resourceId).orElse(null);
            if (resource != null) {
                booking.setResource(resource);
            }
            bookings.add(service.createBooking(booking));
        }
        System.out.println("[BookingController] Created multi-slot bookings for customer ID: " + customerId + ", name: " + customerName);
        return bookings;
    }

    @GetMapping("/customer/{customerId}")
    public List<Booking> getBookingsByCustomer(@PathVariable Long customerId) {
        System.out.println("[BookingController] ===== getBookingsByCustomer called ======");
        System.out.println("[BookingController] Customer ID: " + customerId);
        
        // Get bookings by customerId
        List<Booking> bookings = service.getBookingsByCustomer(customerId);
        System.out.println("[BookingController] Found " + bookings.size() + " bookings by customerId: " + customerId);
        
        // Populate resourceId for each booking (for frontend compatibility)
        for (Booking booking : bookings) {
            if (booking.getResource() != null) {
                booking.setResourceId(booking.getResource().getId());
            }
        }
        
        // If no bookings found by customerId, try to find by email as fallback (for existing bookings without customerId)
        if (bookings.isEmpty()) {
            System.out.println("[BookingController] No bookings found by customerId, trying email fallback...");
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("[BookingController] Authentication: " + (authentication != null ? "present" : "null"));
                if (authentication != null && authentication.getName() != null) {
                    String username = authentication.getName(); // This is the email
                    System.out.println("[BookingController] Authenticated username (email): " + username);
                    User currentUser = userRepository.findByEmail(username).orElse(null);
                    if (currentUser != null && currentUser.getEmail() != null) {
                        System.out.println("[BookingController] No bookings found by customerId, trying email: " + currentUser.getEmail());
                        List<Booking> bookingsByEmail = service.getBookingsByCustomerEmail(currentUser.getEmail());
                        System.out.println("[BookingController] Found " + bookingsByEmail.size() + " bookings by email: " + currentUser.getEmail());
                        // Update these bookings to have the correct customerId for future queries
                        for (Booking booking : bookingsByEmail) {
                            if (booking.getCustomerId() == null) {
                                booking.setCustomerId(customerId);
                                bookingRepository.save(booking);
                                System.out.println("[BookingController] Updated booking " + booking.getId() + " with customerId: " + customerId);
                            }
                            // Populate resourceId for frontend compatibility
                            if (booking.getResource() != null) {
                                booking.setResourceId(booking.getResource().getId());
                            }
                        }
                        System.out.println("[BookingController] Returning " + bookingsByEmail.size() + " bookings found by email");
                        return bookingsByEmail;
                    } else {
                        System.out.println("[BookingController] Current user not found for email: " + username);
                    }
                } else {
                    System.out.println("[BookingController] Authentication is null or username is null");
                }
            } catch (Exception e) {
                System.err.println("[BookingController] Error getting bookings by email: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("[BookingController] Returning " + bookings.size() + " bookings found by customerId");
        }
        
        return bookings;
    }

    @GetMapping("/resource/{resourceId}")
    public List<Booking> getBookingsByResource(@PathVariable Long resourceId) {
        return service.getBookingsByResource(resourceId);
    }

    @GetMapping("/provider/me")
    public List<Booking> getBookingsByCurrentProvider() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                String username = authentication.getName(); // This is the email
                User currentUser = userRepository.findByEmail(username).orElse(null);
                if (currentUser != null) {
                    System.out.println("[BookingController] Getting bookings for provider: " + currentUser.getId());
                    return service.getBookingsByProvider(currentUser.getId());
                }
            }
            System.out.println("[BookingController] No authenticated provider found");
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[BookingController] Error getting provider bookings: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        service.deleteBooking(bookingId);
    }

    @PutMapping("/{bookingId}/cancel")
    public void cancelBooking(@PathVariable Long bookingId) {
        service.cancelBooking(bookingId);
    }

    @PutMapping("/provider/{providerId}/edit/{bookingId}")
    public Booking providerEditBooking(@PathVariable Long providerId, @PathVariable Long bookingId,
            @RequestBody Booking updated) {
        // Only allow status and time changes, not customer info
        Booking booking = service.getBookingById(bookingId);
        if (booking != null && booking.getResource() != null
                && booking.getResource().getProviderId().equals(providerId)) {
            booking.setStatus(updated.getStatus());
            booking.setStartTime(updated.getStartTime());
            booking.setEndTime(updated.getEndTime());
            return service.saveBooking(booking);
        }
        return null;
    }

    @PutMapping("/provider/{providerId}/cancel/{bookingId}")
    public Booking providerCancelBooking(@PathVariable Long providerId, @PathVariable Long bookingId) {
        Booking booking = service.getBookingById(bookingId);
        if (booking != null && booking.getResource() != null
                && booking.getResource().getProviderId().equals(providerId)) {
            booking.setStatus("cancelled");
            return service.saveBooking(booking);
        }
        return null;
    }
}