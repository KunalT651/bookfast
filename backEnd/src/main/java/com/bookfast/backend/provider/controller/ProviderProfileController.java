
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
public class ProviderProfileController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public ProviderProfileController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // This is the email

        User currentUser = authService.getUserByEmail(username);

        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "id", currentUser.getId(),
                "firstName", currentUser.getFirstName(),
                "lastName", currentUser.getLastName(),
                "email", currentUser.getEmail(),
                "organizationName", currentUser.getOrganizationName(),
                "serviceCategory", currentUser.getServiceCategory()
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody Map<String, String> updates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // This is the email

        User currentUser = authService.getUserByEmail(username);

        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Update fields if provided
        if (updates.containsKey("firstName")) {
            currentUser.setFirstName(updates.get("firstName"));
        }
        if (updates.containsKey("lastName")) {
            currentUser.setLastName(updates.get("lastName"));
        }
        // Email update might require re-authentication or additional verification logic
        // For now, let's assume email is not directly updatable via this endpoint easily
        // or requires more complex logic (e.g., re-verifying new email)
        // if (updates.containsKey("email")) {
        //     currentUser.setEmail(updates.get("email"));
        // }

        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "id", currentUser.getId(),
                "firstName", currentUser.getFirstName(),
                "lastName", currentUser.getLastName(),
                "email", currentUser.getEmail(),
                "organizationName", currentUser.getOrganizationName(),
                "serviceCategory", currentUser.getServiceCategory()
        ));
    }
}