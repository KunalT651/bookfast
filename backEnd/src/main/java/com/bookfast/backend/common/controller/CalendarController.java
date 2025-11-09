package com.bookfast.backend.common.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.provider.service.GoogleCalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class CalendarController {
    
    private final GoogleCalendarService googleCalendarService;
    private final AuthService authService;

    public CalendarController(GoogleCalendarService googleCalendarService, AuthService authService) {
        this.googleCalendarService = googleCalendarService;
        this.authService = authService;
    }

    /**
     * Generate Google OAuth authorization URL
     */
    @GetMapping("/auth-url")
    public ResponseEntity<?> getAuthUrl() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            // Validate Gmail address
            if (!googleCalendarService.isValidGmailAddress(user.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Google Calendar integration requires a Gmail address",
                    "currentEmail", user.getEmail(),
                    "suggestion", "Please use a Gmail account (e.g., yourname@gmail.com) to enable calendar integration"
                ));
            }

            String authUrl = googleCalendarService.generateAuthUrl(user.getId(), user.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "authUrl", authUrl,
                "message", "Click the URL to authorize BookFast to access your Google Calendar"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to generate auth URL: " + e.getMessage()));
        }
    }

    /**
     * Exchange authorization code for tokens
     */
    @PostMapping("/connect")
    public ResponseEntity<?> connectCalendar(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            String authCode = request.get("authCode");
            if (authCode == null || authCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Authorization code is required"));
            }

            String result = googleCalendarService.exchangeCodeForTokens(user.getId(), authCode);
            
            return ResponseEntity.ok(Map.of(
                "message", result,
                "connected", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to connect calendar: " + e.getMessage()));
        }
    }

    /**
     * Handle OAuth callback from Google
     */
    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            String state = request.get("state");
            
            if (code == null || state == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing authorization code or state"));
            }
            
            // Extract user ID from state parameter
            Long userId = Long.parseLong(state);
            
            String result = googleCalendarService.exchangeCodeForTokens(userId, code);
            
            return ResponseEntity.ok(Map.of(
                "message", result,
                "connected", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to process callback: " + e.getMessage()));
        }
    }

    /**
     * Check calendar connection status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getCalendarStatus() {
        try {
            System.out.println("=== CALENDAR STATUS DEBUG ===");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + authentication);
            String email = authentication.getName();
            System.out.println("Email from authentication: " + email);
            
            User user = authService.getUserByEmail(email);
            System.out.println("User found: " + (user != null ? user.getEmail() : "null"));
            
            if (user == null) {
                System.out.println("❌ User not found for email: " + email);
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            // Simplified response without calling GoogleCalendarService methods
            boolean isValidGmail = user.getEmail() != null && 
                (user.getEmail().toLowerCase().endsWith("@gmail.com") || 
                 user.getEmail().toLowerCase().endsWith("@googlemail.com"));
            
            System.out.println("Is Gmail: " + isValidGmail);
            System.out.println("=== END CALENDAR STATUS DEBUG ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("connected", user.getCalendarConnected() != null ? user.getCalendarConnected() : false);
            response.put("isGmail", isValidGmail);
            response.put("email", user.getEmail());
            response.put("calendarEmail", user.getCalendarEmail() != null ? user.getCalendarEmail() : "");
            response.put("canConnect", isValidGmail);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error in getCalendarStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to get calendar status: " + e.getMessage()));
        }
    }

    /**
     * Disconnect Google Calendar
     */
    @PostMapping("/disconnect")
    public ResponseEntity<?> disconnectCalendar() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            googleCalendarService.disconnectCalendar(user.getId());
            
            return ResponseEntity.ok(Map.of(
                "message", "Google Calendar disconnected successfully",
                "connected", false
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to disconnect calendar: " + e.getMessage()));
        }
    }

    /**
     * Create calendar event (for bookings)
     */
    @PostMapping("/event")
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            if (!googleCalendarService.isCalendarConnected(user.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Google Calendar not connected"));
            }

            String title = (String) request.get("title");
            String startTimeStr = (String) request.get("startTime");
            String endTimeStr = (String) request.get("endTime");

            if (title == null || startTimeStr == null || endTimeStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title, startTime, and endTime are required"));
            }

            // Parse datetime strings (assuming ISO format)
            java.time.LocalDateTime startTime = java.time.LocalDateTime.parse(startTimeStr);
            java.time.LocalDateTime endTime = java.time.LocalDateTime.parse(endTimeStr);

            String eventId = googleCalendarService.createCalendarEvent(user.getId(), title, startTime, endTime);
            
            return ResponseEntity.ok(Map.of(
                "eventId", eventId,
                "message", "Calendar event created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create calendar event: " + e.getMessage()));
        }
    }

    /**
     * Update calendar event
     */
    @PutMapping("/event/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable String eventId, @RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            if (!googleCalendarService.isCalendarConnected(user.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Google Calendar not connected"));
            }

            String title = (String) request.get("title");
            String startTimeStr = (String) request.get("startTime");
            String endTimeStr = (String) request.get("endTime");

            if (title == null || startTimeStr == null || endTimeStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title, startTime, and endTime are required"));
            }

            java.time.LocalDateTime startTime = java.time.LocalDateTime.parse(startTimeStr);
            java.time.LocalDateTime endTime = java.time.LocalDateTime.parse(endTimeStr);

            googleCalendarService.updateCalendarEvent(user.getId(), eventId, title, startTime, endTime);
            
            return ResponseEntity.ok(Map.of("message", "Calendar event updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update calendar event: " + e.getMessage()));
        }
    }

    /**
     * Delete calendar event
     */
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable String eventId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            if (!googleCalendarService.isCalendarConnected(user.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Google Calendar not connected"));
            }

            googleCalendarService.deleteCalendarEvent(user.getId(), eventId);
            
            return ResponseEntity.ok(Map.of("message", "Calendar event deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete calendar event: " + e.getMessage()));
        }
    }
}
