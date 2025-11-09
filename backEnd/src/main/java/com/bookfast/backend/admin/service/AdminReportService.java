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

    /**
     * Export report data to CSV format
     */
    public byte[] exportReportToCSV(String reportType, String period) {
        try {
            StringBuilder csv = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            switch (reportType.toLowerCase()) {
                case "users":
                    csv.append("ID,First Name,Last Name,Email,Role,Created Date,Active\n");
                    List<User> users = userRepository.findAll();
                    for (User user : users) {
                        csv.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%b\n",
                            user.getId(),
                            escapeCsv(user.getFirstName()),
                            escapeCsv(user.getLastName()),
                            escapeCsv(user.getEmail()),
                            user.getRole() != null ? user.getRole().getName() : "N/A",
                            user.getCreatedDate() != null ? user.getCreatedDate().format(formatter) : "N/A",
                            user.getIsActive() != null ? user.getIsActive() : false
                        ));
                    }
                    break;
                    
                case "bookings":
                    csv.append("Booking ID,Customer Name,Email,Resource,Start Time,End Time,Status,Amount,Payment Status\n");
                    List<Booking> bookings = bookingRepository.findAll();
                    for (Booking booking : bookings) {
                        csv.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\"\n",
                            booking.getId(),
                            escapeCsv(booking.getCustomerName()),
                            escapeCsv(booking.getCustomerEmail()),
                            booking.getResource() != null ? escapeCsv(booking.getResource().getName()) : "N/A",
                            booking.getStartTime() != null ? booking.getStartTime().format(formatter) : "N/A",
                            booking.getEndTime() != null ? booking.getEndTime().format(formatter) : "N/A",
                            escapeCsv(booking.getStatus()),
                            booking.getFinalAmount() != null ? booking.getFinalAmount() : 0.0,
                            escapeCsv(booking.getPaymentStatus())
                        ));
                    }
                    break;
                    
                case "revenue":
                    csv.append("Payment ID,Booking ID,Amount,Payment Date,Payment Method,Status\n");
                    List<Payment> payments = paymentRepository.findAll();
                    for (Payment payment : payments) {
                        csv.append(String.format("%d,%d,%.2f,\"%s\",\"%s\",\"%s\"\n",
                            payment.getId(),
                            payment.getBooking() != null ? payment.getBooking().getId() : 0,
                            payment.getAmount() != null ? payment.getAmount() : 0.0,
                            payment.getPaymentDate() != null ? payment.getPaymentDate().format(formatter) : "N/A",
                            escapeCsv(payment.getPaymentMethod()),
                            escapeCsv(payment.getPaymentStatus())
                        ));
                    }
                    break;
                    
                case "providers":
                    csv.append("ID,Name,Email,Organization,Service Category,Created Date,Active\n");
                    List<User> providers = userRepository.findAll().stream()
                        .filter(u -> u.getRole() != null && "PROVIDER".equalsIgnoreCase(u.getRole().getName()))
                        .toList();
                    for (User provider : providers) {
                        csv.append(String.format("%d,\"%s %s\",\"%s\",\"%s\",\"%s\",\"%s\",%b\n",
                            provider.getId(),
                            escapeCsv(provider.getFirstName()),
                            escapeCsv(provider.getLastName()),
                            escapeCsv(provider.getEmail()),
                            escapeCsv(provider.getOrganizationName()),
                            escapeCsv(provider.getServiceCategory()),
                            provider.getCreatedDate() != null ? provider.getCreatedDate().format(formatter) : "N/A",
                            provider.getIsActive() != null ? provider.getIsActive() : false
                        ));
                    }
                    break;
                    
                default:
                    csv.append("System Overview Report\n");
                    csv.append("Generated," + LocalDateTime.now().format(formatter) + "\n\n");
                    Map<String, Object> reports = getSystemReports(period);
                    csv.append("Metric,Value\n");
                    csv.append("Total Users," + reports.get("totalUsers") + "\n");
                    csv.append("Total Providers," + reports.get("totalProviders") + "\n");
                    csv.append("Total Bookings," + reports.get("totalBookings") + "\n");
                    csv.append("Total Revenue,$" + reports.get("totalRevenue") + "\n");
                    break;
            }
            
            return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV: " + e.getMessage(), e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Escape quotes and wrap in quotes if contains comma
        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value + "\"";
        }
        return value;
    }
}
