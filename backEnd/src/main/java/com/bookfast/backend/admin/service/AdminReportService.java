package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminReportService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public AdminReportService(UserRepository userRepository, BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    public Map<String, Object> getSystemReports(String period) {
        Map<String, Object> reports = new HashMap<>();
        
        // Calculate date range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(Integer.parseInt(period), ChronoUnit.DAYS);
        
        // Get counts
        long totalUsers = userRepository.count();
        long totalProviders = userRepository.countByRoleNameIgnoreCase("PROVIDER");
        long totalBookings = bookingRepository.count();
        long totalPayments = paymentRepository.count();
        
        // Get period-specific counts
        long newUsers = userRepository.countByCreatedDateAfter(startDate);
        long newProviders = userRepository.countByRoleNameIgnoreCaseAndCreatedDateAfter("PROVIDER", startDate);
        long newBookings = bookingRepository.countByDateAfter(startDate);
        
        // Calculate revenue (simplified)
        double totalRevenue = paymentRepository.findAll().stream()
            .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
            .sum();
        
        double periodRevenue = paymentRepository.findByPaymentDateAfter(startDate).stream()
            .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
            .sum();
        
        reports.put("totalUsers", totalUsers);
        reports.put("totalProviders", totalProviders);
        reports.put("totalBookings", totalBookings);
        reports.put("totalRevenue", totalRevenue);
        
        reports.put("newUsers", newUsers);
        reports.put("newProviders", newProviders);
        reports.put("newBookings", newBookings);
        reports.put("periodRevenue", periodRevenue);
        
        // Mock data for charts and detailed reports
        reports.put("userGrowthData", generateMockChartData(7));
        reports.put("bookingTrendsData", generateMockChartData(7));
        reports.put("revenueData", generateMockChartData(7));
        
        // Top providers (mock data for now)
        reports.put("topProviders", generateMockTopProviders());
        
        // Recent activity (mock data)
        reports.put("recentActivity", generateMockRecentActivity());
        
        return reports;
    }

    public Map<String, Object> getDashboardStats() {
        return getSystemReports("30");
    }

    public Map<String, Object> getUserReports(String period) {
        Map<String, Object> reports = new HashMap<>();
        reports.put("totalUsers", userRepository.count());
        reports.put("newUsers", userRepository.countByCreatedDateAfter(
            LocalDateTime.now().minus(Integer.parseInt(period), ChronoUnit.DAYS)));
        reports.put("userGrowthData", generateMockChartData(Integer.parseInt(period)));
        return reports;
    }

    public Map<String, Object> getBookingReports(String period) {
        Map<String, Object> reports = new HashMap<>();
        reports.put("totalBookings", bookingRepository.count());
        reports.put("newBookings", bookingRepository.countByDateAfter(
            LocalDateTime.now().minus(Integer.parseInt(period), ChronoUnit.DAYS)));
        reports.put("bookingTrendsData", generateMockChartData(Integer.parseInt(period)));
        return reports;
    }

    public Map<String, Object> getRevenueReports(String period) {
        Map<String, Object> reports = new HashMap<>();
        double totalRevenue = paymentRepository.findAll().stream()
            .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
            .sum();
        reports.put("totalRevenue", totalRevenue);
        reports.put("revenueData", generateMockChartData(Integer.parseInt(period)));
        return reports;
    }

    public Map<String, Object> getProviderReports(String period) {
        Map<String, Object> reports = new HashMap<>();
        reports.put("totalProviders", userRepository.countByRoleNameIgnoreCase("PROVIDER"));
        reports.put("topProviders", generateMockTopProviders());
        return reports;
    }

    private List<Map<String, Object>> generateMockChartData(int days) {
        return List.of(
            Map.of("date", "2024-01-01", "value", 10),
            Map.of("date", "2024-01-02", "value", 15),
            Map.of("date", "2024-01-03", "value", 12),
            Map.of("date", "2024-01-04", "value", 18),
            Map.of("date", "2024-01-05", "value", 20),
            Map.of("date", "2024-01-06", "value", 16),
            Map.of("date", "2024-01-07", "value", 22)
        );
    }

    private List<Map<String, Object>> generateMockTopProviders() {
        return List.of(
            Map.of("name", "Tech Solutions Inc", "bookings", 45, "revenue", 12500.50, "rating", 4.8),
            Map.of("name", "Creative Design Co", "bookings", 38, "revenue", 9800.25, "rating", 4.6),
            Map.of("name", "Marketing Pro", "bookings", 32, "revenue", 8750.75, "rating", 4.7),
            Map.of("name", "Consulting Group", "bookings", 28, "revenue", 7200.00, "rating", 4.5)
        );
    }

    private List<Map<String, Object>> generateMockRecentActivity() {
        return List.of(
            Map.of("description", "New user registered", "timestamp", LocalDateTime.now().minusHours(1), "icon", "fa-user-plus"),
            Map.of("description", "Provider created new resource", "timestamp", LocalDateTime.now().minusHours(2), "icon", "fa-plus-circle"),
            Map.of("description", "Booking completed", "timestamp", LocalDateTime.now().minusHours(3), "icon", "fa-check-circle"),
            Map.of("description", "Payment processed", "timestamp", LocalDateTime.now().minusHours(4), "icon", "fa-credit-card"),
            Map.of("description", "Review submitted", "timestamp", LocalDateTime.now().minusHours(5), "icon", "fa-star")
        );
    }
}
