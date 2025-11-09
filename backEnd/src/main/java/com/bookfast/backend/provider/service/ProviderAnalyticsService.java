package com.bookfast.backend.provider.service;

import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.model.Payment;
import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.model.Review;
import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.repository.PaymentRepository;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.resource.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProviderAnalyticsService {

    private final ResourceRepository resourceRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    public ProviderAnalyticsService(ResourceRepository resourceRepository,
                                   BookingRepository bookingRepository,
                                   PaymentRepository paymentRepository,
                                   ReviewRepository reviewRepository) {
        this.resourceRepository = resourceRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.reviewRepository = reviewRepository;
    }

    public Map<String, Object> getProviderAnalytics(Long providerId, String period) {
        Map<String, Object> analytics = new HashMap<>();

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(Integer.parseInt(period), ChronoUnit.DAYS);

        // Get all resources for this provider
        List<Resource> providerResources = resourceRepository.findByProviderId(providerId);
        List<Long> resourceIds = providerResources.stream().map(Resource::getId).toList();

        // Get all bookings for these resources
        List<Booking> allBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getResource() != null && resourceIds.contains(b.getResource().getId()))
                .toList();

        // Filter bookings by period
        List<Booking> periodBookings = allBookings.stream()
                .filter(b -> b.getStartTime() != null && !b.getStartTime().isBefore(startDate))
                .toList();

        // Calculate booking statistics
        analytics.put("totalBookings", allBookings.size());
        analytics.put("periodBookings", periodBookings.size());
        analytics.put("confirmedBookings", periodBookings.stream().filter(b -> "confirmed".equalsIgnoreCase(b.getStatus())).count());
        analytics.put("cancelledBookings", periodBookings.stream().filter(b -> "cancelled".equalsIgnoreCase(b.getStatus())).count());
        analytics.put("pendingBookings", periodBookings.stream().filter(b -> "pending".equalsIgnoreCase(b.getStatus())).count());

        // Calculate revenue statistics
        List<Payment> allPayments = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking() != null && p.getBooking().getResource() != null && 
                            resourceIds.contains(p.getBooking().getResource().getId()))
                .toList();

        double totalRevenue = allPayments.stream()
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        
        double periodRevenue = allPayments.stream()
                .filter(p -> p.getPaymentDate() != null && !p.getPaymentDate().isBefore(startDate))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        analytics.put("totalRevenue", String.format("%.2f", totalRevenue));
        analytics.put("periodRevenue", String.format("%.2f", periodRevenue));
        analytics.put("averageBookingValue", periodBookings.size() > 0 ? 
            String.format("%.2f", periodRevenue / periodBookings.size()) : "0.00");

        // Calculate review statistics
        List<Review> allReviews = reviewRepository.findAll().stream()
                .filter(r -> r.getResource() != null && resourceIds.contains(r.getResource().getId()))
                .toList();
        
        double averageRating = allReviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        analytics.put("averageRating", String.format("%.1f", averageRating));
        analytics.put("totalReviews", allReviews.size());

        // Top 5 Resources by booking count
        List<Map<String, Object>> topResources = allBookings.stream()
                .filter(b -> b.getResource() != null)
                .collect(Collectors.groupingBy(b -> b.getResource().getName(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> Map.of("name", (Object)entry.getKey(), "bookings", (Object)entry.getValue()))
                .toList();
        analytics.put("topResources", topResources);

        // Recent 5 Bookings
        List<Map<String, Object>> recentBookings = allBookings.stream()
                .sorted(Comparator.comparing(Booking::getStartTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(b -> Map.of(
                        "id", (Object)b.getId(),
                        "customerName", (Object)b.getCustomerName(),
                        "resourceName", (Object)(b.getResource() != null ? b.getResource().getName() : "N/A"),
                        "startTime", (Object)(b.getStartTime() != null ? 
                            b.getStartTime().format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a")) : "N/A"),
                        "status", (Object)b.getStatus()
                ))
                .toList();
        analytics.put("recentBookings", recentBookings);

        return analytics;
    }
}

