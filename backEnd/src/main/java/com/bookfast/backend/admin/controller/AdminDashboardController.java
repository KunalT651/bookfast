package com.bookfast.backend.admin.controller;

import com.bookfast.backend.admin.service.AdminDashboardService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminDashboardController {
    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            Map<String, Object> dashboardData = new HashMap<>();
            
            // Get basic statistics
            dashboardData.put("totalUsers", dashboardService.getTotalUsers());
            dashboardData.put("totalProviders", dashboardService.getTotalProviders());
            dashboardData.put("totalCustomers", dashboardService.getTotalCustomers());
            dashboardData.put("totalBookings", dashboardService.getTotalBookings());
            dashboardData.put("totalReviews", dashboardService.getTotalReviews());
            dashboardData.put("totalRevenue", dashboardService.getTotalRevenue());
            
            // Get recent activity
            dashboardData.put("recentBookings", dashboardService.getRecentBookings());
            dashboardData.put("recentReviews", dashboardService.getRecentReviews());
            
            // System stats
            Map<String, Object> systemStats = new HashMap<>();
            systemStats.put("uptime", "99.9%");
            systemStats.put("status", "Online");
            systemStats.put("lastBackup", java.time.LocalDate.now().toString());
            dashboardData.put("systemStats", systemStats);
            
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            System.out.println("[AdminDashboardController] Error getting dashboard data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to load dashboard data"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", dashboardService.getTotalUsers());
            stats.put("totalProviders", dashboardService.getTotalProviders());
            stats.put("totalCustomers", dashboardService.getTotalCustomers());
            stats.put("totalBookings", dashboardService.getTotalBookings());
            stats.put("totalReviews", dashboardService.getTotalReviews());
            stats.put("totalRevenue", dashboardService.getTotalRevenue());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.out.println("[AdminDashboardController] Error getting system stats: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to load system stats"));
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<Map<String, Object>> getRecentActivity() {
        try {
            Map<String, Object> activity = new HashMap<>();
            activity.put("recentBookings", dashboardService.getRecentBookings());
            activity.put("recentReviews", dashboardService.getRecentReviews());
            
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            System.out.println("[AdminDashboardController] Error getting recent activity: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to load recent activity"));
        }
    }
}
