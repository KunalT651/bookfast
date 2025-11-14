package com.bookfast.backend.provider.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.provider.model.UnavailableDate;
import com.bookfast.backend.provider.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/provider/unavailable-dates")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class UnavailableDateController {

    private final ProviderService providerService;
    private final AuthService authService;

    public UnavailableDateController(ProviderService providerService, AuthService authService) {
        this.providerService = providerService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getUnavailableDates() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = authService.getUserByEmail(username);
            if (currentUser == null) {
                return ResponseEntity.notFound().build();
            }

            List<UnavailableDate> dates = providerService.getUnavailableDates(currentUser.getId());
            return ResponseEntity.ok(dates);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to fetch unavailable dates: " + ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> markUnavailableDates(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = authService.getUserByEmail(username);
            if (currentUser == null) {
                return ResponseEntity.notFound().build();
            }

            String startDate = request.get("startDate");
            String endDate = request.get("endDate");
            String reason = request.get("reason");

            providerService.markUnavailableDates(currentUser.getId(), startDate, endDate, reason);
            return ResponseEntity.ok(Map.of("message", "Unavailable dates marked successfully"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to mark unavailable dates: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUnavailableDate(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = authService.getUserByEmail(username);
            if (currentUser == null) {
                return ResponseEntity.notFound().build();
            }

            providerService.removeUnavailableDate(id, currentUser.getId());
            return ResponseEntity.ok(Map.of("message", "Unavailable date removed successfully"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to remove unavailable date: " + ex.getMessage()));
        }
    }
}

