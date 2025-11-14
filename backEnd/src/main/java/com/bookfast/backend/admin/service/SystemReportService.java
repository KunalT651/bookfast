package com.bookfast.backend.admin.service;

import com.bookfast.backend.resource.repository.BookingRepository;
import com.bookfast.backend.resource.repository.ReviewRepository;
import com.bookfast.backend.resource.repository.CustomerRepository;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class SystemReportService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getSystemReports() {
        Map<String, Object> reports = new HashMap<>();
        reports.put("totalBookings", bookingRepository.count());
        reports.put("totalRevenue", bookingRepository.sumTotalAmount());
        reports.put("totalUsers", userRepository.count());
        reports.put("totalProviders", userRepository.countByRoleName("PROVIDER")); // Updated line
        reports.put("totalCustomers", customerRepository.count());
        reports.put("totalReviews", reviewRepository.count());
        reports.put("totalCancellations", bookingRepository.countByStatus("CANCELLED"));
        // Add more stats as needed
        return reports;
    }
}
