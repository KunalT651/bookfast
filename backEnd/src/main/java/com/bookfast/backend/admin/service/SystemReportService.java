package com.bookfast.backend.admin.service;

import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.repository.ReviewRepository;
import com.bookfast.backend.resource.repository.CustomerRepository;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.resource.repository.PaymentRepository;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class SystemReportService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getSystemReports() {
        Map<String, Object> reports = new HashMap<>();
        
        // Basic counts
        reports.put("totalBookings", bookingRepository.count());
        reports.put("totalRevenue", bookingRepository.sumTotalAmount() != null ? bookingRepository.sumTotalAmount() : 0.0);
        reports.put("totalUsers", userRepository.count());
        reports.put("totalProviders", userRepository.countByRoleName("PROVIDER"));
        reports.put("totalCustomers", userRepository.countByRoleName("CUSTOMER"));
        reports.put("totalResources", resourceRepository.count());
        reports.put("totalReviews", reviewRepository.count());
        reports.put("totalPayments", paymentRepository.count());
        
        // Booking status breakdown
        reports.put("confirmedBookings", bookingRepository.countByStatus("confirmed"));
        reports.put("pendingBookings", bookingRepository.countByStatus("pending"));
        reports.put("cancelledBookings", bookingRepository.countByStatus("cancelled"));
        
        // Recent activity (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        reports.put("recentBookings", bookingRepository.countByDateAfter(thirtyDaysAgo));
        reports.put("recentUsers", userRepository.countByCreatedDateAfter(thirtyDaysAgo));
        reports.put("recentReviews", reviewRepository.countByDateAfter(thirtyDaysAgo.toLocalDate()));
        
        // Average ratings
        Double avgRating = reviewRepository.getAverageRating();
        reports.put("averageRating", avgRating != null ? avgRating : 0.0);
        
        // Payment status
        reports.put("paidBookings", bookingRepository.countByPaymentStatus("paid"));
        reports.put("pendingPayments", bookingRepository.countByPaymentStatus("pending"));
        
        // Top performing metrics
        reports.put("totalRevenueThisMonth", getMonthlyRevenue());
        reports.put("bookingGrowthRate", getBookingGrowthRate());
        
        return reports;
    }
    
    private Double getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1);
        return bookingRepository.sumTotalAmountByDateAfter(startOfMonth.toLocalDate());
    }
    
    private Double getBookingGrowthRate() {
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime lastMonth = thisMonth.minusMonths(1);
        LocalDateTime twoMonthsAgo = lastMonth.minusMonths(1);
        
        long thisMonthBookings = bookingRepository.countByDateAfter(thisMonth);
        long lastMonthBookings = bookingRepository.countByDateBetween(lastMonth.toLocalDate(), thisMonth.toLocalDate());
        
        if (lastMonthBookings == 0) return 0.0;
        return ((double) (thisMonthBookings - lastMonthBookings) / lastMonthBookings) * 100;
    }
}
