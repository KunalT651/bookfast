package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.repository.ReviewRepository;
import com.bookfast.backend.resource.repository.PaymentRepository;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.model.Review;
import com.bookfast.backend.resource.model.Payment;
import com.bookfast.backend.common.model.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminDashboardService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalProviders() {
        return userRepository.countByRoleName("PROVIDER");
    }

    public long getTotalCustomers() {
        return userRepository.countByRoleName("CUSTOMER");
    }

    public long getTotalBookings() {
        return bookingRepository.count();
    }

    public long getTotalReviews() {
        return reviewRepository.count();
    }

    public double getTotalRevenue() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
                .sum();
    }

    public List<Map<String, Object>> getRecentBookings() {
        List<Booking> recentBookings = bookingRepository.findTop10ByOrderByDateDesc();
        List<Map<String, Object>> bookingData = new ArrayList<>();
        
        for (Booking booking : recentBookings) {
            Map<String, Object> bookingInfo = new HashMap<>();
            bookingInfo.put("id", booking.getId());
            bookingInfo.put("customerName", booking.getCustomerName());
            bookingInfo.put("resourceName", booking.getResource() != null ? booking.getResource().getName() : "N/A");
            bookingInfo.put("date", booking.getDate());
            bookingInfo.put("status", booking.getStatus());
            bookingInfo.put("amount", booking.getFinalAmount() != null ? booking.getFinalAmount() : 0.0);
            bookingData.add(bookingInfo);
        }
        
        return bookingData;
    }

    public List<Map<String, Object>> getRecentReviews() {
        List<Review> recentReviews = reviewRepository.findTop10ByOrderByDateDesc();
        List<Map<String, Object>> reviewData = new ArrayList<>();
        
        for (Review review : recentReviews) {
            Map<String, Object> reviewInfo = new HashMap<>();
            reviewInfo.put("id", review.getId());
            reviewInfo.put("customerName", review.getCustomerName());
            reviewInfo.put("resourceName", review.getResource() != null ? review.getResource().getName() : "N/A");
            reviewInfo.put("rating", review.getRating());
            reviewInfo.put("comment", review.getComment());
            reviewInfo.put("date", review.getDate());
            reviewData.add(reviewInfo);
        }
        
        return reviewData;
    }
}
