package com.bookfast.backend.provider.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.provider.service.ProviderService;
import com.bookfast.backend.provider.service.GoogleCalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class ProviderController {
    private final ProviderService providerService;
    private final GoogleCalendarService googleCalendarService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public ProviderController(ProviderService providerService, GoogleCalendarService googleCalendarService, 
                             AuthService authService, UserRepository userRepository) {
        this.providerService = providerService;
        this.googleCalendarService = googleCalendarService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProviderProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null || !"PROVIDER".equals(user.getRole().getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch profile: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProviderProfile(@RequestBody Map<String, Object> updates) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null || !"PROVIDER".equals(user.getRole().getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            // Update fields
            if (updates.containsKey("firstName")) {
                user.setFirstName((String) updates.get("firstName"));
            }
            if (updates.containsKey("lastName")) {
                user.setLastName((String) updates.get("lastName"));
            }
            if (updates.containsKey("email")) {
                user.setEmail((String) updates.get("email"));
            }
            if (updates.containsKey("organizationName")) {
                user.setOrganizationName((String) updates.get("organizationName"));
            }
            if (updates.containsKey("serviceCategory")) {
                user.setServiceCategory((String) updates.get("serviceCategory"));
            }

            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update profile: " + e.getMessage()));
        }
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null || !"PROVIDER".equals(user.getRole().getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            String imageUrl = providerService.uploadProfilePicture(file);
            user.setImageUrl(imageUrl);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "message", "Profile picture uploaded successfully",
                "url", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload picture: " + e.getMessage()));
        }
    }

    @PostMapping("/calendar/sync")
    public ResponseEntity<?> syncWithGoogleCalendar(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null || !"PROVIDER".equals(user.getRole().getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            String authCode = request.get("authCode");
            String result = googleCalendarService.exchangeCodeForTokens(user.getId(), authCode);
            
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to sync calendar: " + e.getMessage()));
        }
    }

    @PostMapping("/unavailable-dates")
    public ResponseEntity<?> markUnavailableDates(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null || !"PROVIDER".equals(user.getRole().getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            String startDate = (String) request.get("startDate");
            String endDate = (String) request.get("endDate");
            String reason = (String) request.get("reason");
            
            providerService.markUnavailableDates(user.getId(), startDate, endDate, reason);
            
            return ResponseEntity.ok(Map.of("message", "Unavailable dates marked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to mark unavailable dates: " + e.getMessage()));
        }
    }

    @GetMapping("/unavailable-dates")
    public ResponseEntity<?> getUnavailableDates() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null || !"PROVIDER".equals(user.getRole().getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            return ResponseEntity.ok(providerService.getUnavailableDates(user.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch unavailable dates: " + e.getMessage()));
        }
    }

    @DeleteMapping("/unavailable-dates/{id}")
    public ResponseEntity<?> removeUnavailableDate(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            
            if (user == null || !"PROVIDER".equals(user.getRole().getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            providerService.removeUnavailableDate(id, user.getId());
            return ResponseEntity.ok(Map.of("message", "Unavailable date removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to remove unavailable date: " + e.getMessage()));
        }
    }
}
