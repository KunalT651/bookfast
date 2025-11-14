package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.resource.service.BookingService;
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

    public BookingController(BookingService service, ResourceRepository resourceRepository) {
        this.service = service;
        this.resourceRepository = resourceRepository;
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
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
        System.out.println("[BookingController] Creating booking for customer: " + booking.getCustomerName());
        Booking saved = service.createBooking(booking);
        return saved;
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

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable Long bookingId) {
        service.deleteBooking(bookingId);
    }

    @PutMapping("/{bookingId}/cancel")
    public Booking cancelBooking(@PathVariable Long bookingId) {
        return service.cancelBooking(bookingId);
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