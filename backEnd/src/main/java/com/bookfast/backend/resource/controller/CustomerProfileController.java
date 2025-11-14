package com.bookfast.backend.resource.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers/profile")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class CustomerProfileController {
    private final UserRepository userRepository;
    private final AuthService authService;

    public CustomerProfileController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("[CustomerProfileController] Authentication: " + authentication);
            System.out.println("[CustomerProfileController] Authentication name: " + (authentication != null ? authentication.getName() : "null"));
            System.out.println("[CustomerProfileController] Authentication authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));
            
            if (authentication == null || authentication.getName() == null) {
                System.out.println("[CustomerProfileController] No authentication found");
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }
            
            String username = authentication.getName(); // This is the email
            System.out.println("[CustomerProfileController] Looking up user with email: " + username);

            User currentUser = authService.getUserByEmail(username);
            System.out.println("[CustomerProfileController] Found user: " + (currentUser != null ? currentUser.getEmail() : "null"));

            if (currentUser == null) {
                System.out.println("[CustomerProfileController] User not found for email: " + username);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(Map.of(
                    "id", currentUser.getId(),
                    "firstname", currentUser.getFirstName() != null ? currentUser.getFirstName() : "",
                    "lastname", currentUser.getLastName() != null ? currentUser.getLastName() : "",
                    "email", currentUser.getEmail()
            ));
        } catch (Exception e) {
            System.out.println("[CustomerProfileController] Exception in getProfile: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> updates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // This is the email

        User currentUser = authService.getUserByEmail(username);

        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Update fields if provided
        if (updates.containsKey("firstname")) {
            currentUser.setFirstName(updates.get("firstname"));
        }
        if (updates.containsKey("lastname")) {
            currentUser.setLastName(updates.get("lastname"));
        }
        if (updates.containsKey("email")) {
            currentUser.setEmail(updates.get("email"));
        }
        if (updates.containsKey("password") && !updates.get("password").isEmpty()) {
            // Hash the password before storing
            currentUser.setPassword(PasswordUtil.hash(updates.get("password")));
        }

        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "id", currentUser.getId(),
                "firstname", currentUser.getFirstName() != null ? currentUser.getFirstName() : "",
                "lastname", currentUser.getLastName() != null ? currentUser.getLastName() : "",
                "email", currentUser.getEmail()
        ));
    }
}
