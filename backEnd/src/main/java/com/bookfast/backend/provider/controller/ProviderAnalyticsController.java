package com.bookfast.backend.provider.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.provider.service.ProviderAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class ProviderAnalyticsController {

    private final ProviderAnalyticsService analyticsService;
    private final AuthService authService;

    public ProviderAnalyticsController(ProviderAnalyticsService analyticsService, AuthService authService) {
        this.analyticsService = analyticsService;
        this.authService = authService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(@RequestParam(defaultValue = "30") String period) {
        try {
            System.out.println("[ProviderAnalyticsController] Getting analytics for period: " + period);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = authService.getUserByEmail(username);
            if (currentUser == null) {
                System.out.println("[ProviderAnalyticsController] User not found: " + username);
                return ResponseEntity.notFound().build();
            }

            Long providerId = currentUser.getId();
            System.out.println("[ProviderAnalyticsController] Getting analytics for provider ID: " + providerId);

            Map<String, Object> analytics = analyticsService.getProviderAnalytics(providerId, period);
            System.out.println("[ProviderAnalyticsController] Analytics retrieved successfully");

            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            System.err.println("[ProviderAnalyticsController] Error getting analytics: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/earnings")
    public ResponseEntity<?> getEarnings(@RequestParam(defaultValue = "30") String period) {
        try {
            System.out.println("[ProviderAnalyticsController] Getting earnings for period: " + period);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = authService.getUserByEmail(username);
            if (currentUser == null) {
                System.out.println("[ProviderAnalyticsController] User not found: " + username);
                return ResponseEntity.notFound().build();
            }

            Long providerId = currentUser.getId();
            System.out.println("[ProviderAnalyticsController] Getting earnings for provider ID: " + providerId);

            // Earnings uses the same analytics service, but we can filter to just earnings-related data
            Map<String, Object> analytics = analyticsService.getProviderAnalytics(providerId, period);
            
            // Extract earnings-specific data
            Map<String, Object> earnings = new HashMap<>();
            earnings.put("totalRevenue", analytics.getOrDefault("totalRevenue", "0.00"));
            earnings.put("periodRevenue", analytics.getOrDefault("periodRevenue", "0.00"));
            earnings.put("averageBookingValue", analytics.getOrDefault("averageBookingValue", "0.00"));
            earnings.put("totalBookings", analytics.getOrDefault("totalBookings", 0));
            earnings.put("periodBookings", analytics.getOrDefault("periodBookings", 0));
            earnings.put("confirmedBookings", analytics.getOrDefault("confirmedBookings", 0));
            earnings.put("pendingBookings", analytics.getOrDefault("pendingBookings", 0));
            earnings.put("cancelledBookings", analytics.getOrDefault("cancelledBookings", 0));
            
            System.out.println("[ProviderAnalyticsController] Earnings retrieved successfully");
            return ResponseEntity.ok(earnings);
        } catch (Exception e) {
            System.err.println("[ProviderAnalyticsController] Error getting earnings: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}

