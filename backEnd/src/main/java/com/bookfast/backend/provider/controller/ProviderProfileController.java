
package com.bookfast.backend.provider.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/provider/profile")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class ProviderProfileController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public ProviderProfileController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try {
            System.out.println("[ProviderProfileController] Getting profile for current user");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = authService.getUserByEmail(username);

            if (currentUser == null) {
                System.out.println("[ProviderProfileController] User not found: " + username);
                return ResponseEntity.notFound().build();
            }

            System.out.println("[ProviderProfileController] Profile retrieved successfully for user: " + username);
            return ResponseEntity.ok(Map.of(
                    "firstName", currentUser.getFirstName() != null ? currentUser.getFirstName() : "",
                    "lastName", currentUser.getLastName() != null ? currentUser.getLastName() : "",
                    "email", currentUser.getEmail() != null ? currentUser.getEmail() : "",
                    "imageUrl", currentUser.getImageUrl() != null ? currentUser.getImageUrl() : ""
            ));
        } catch (Exception e) {
            System.err.println("[ProviderProfileController] Error getting profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody Map<String, String> updates) {
        try {
            System.out.println("[ProviderProfileController] Updating profile with data: " + updates);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = authService.getUserByEmail(username);

            if (currentUser == null) {
                System.out.println("[ProviderProfileController] User not found: " + username);
                return ResponseEntity.notFound().build();
            }

            // Update fields if provided
            if (updates.containsKey("firstName")) {
                currentUser.setFirstName(updates.get("firstName"));
            }
            if (updates.containsKey("lastName")) {
                currentUser.setLastName(updates.get("lastName"));
            }
            if (updates.containsKey("email")) {
                String newEmail = updates.get("email");
                // Check if email is already taken by another user
                if (userRepository.findByEmail(newEmail).isPresent() && 
                    !newEmail.equals(currentUser.getEmail())) {
                    System.out.println("[ProviderProfileController] Email already exists: " + newEmail);
                    return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
                }
                currentUser.setEmail(newEmail);
            }

            userRepository.save(currentUser);
            System.out.println("[ProviderProfileController] Profile updated successfully for user: " + username);

            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "firstName", currentUser.getFirstName() != null ? currentUser.getFirstName() : "",
                    "lastName", currentUser.getLastName() != null ? currentUser.getLastName() : "",
                    "email", currentUser.getEmail() != null ? currentUser.getEmail() : ""
            ));
        } catch (Exception e) {
            System.err.println("[ProviderProfileController] Error updating profile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}