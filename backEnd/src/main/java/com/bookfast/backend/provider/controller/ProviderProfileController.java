
package com.bookfast.backend.provider.controller;

import com.bookfast.backend.common.auth.service.JwtService;

import com.bookfast.backend.common.model.ProviderProfile;
import com.bookfast.backend.common.repository.ProviderProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider")
public class ProviderProfileController {
    // Endpoint to get current provider profile using JWT
    private final JwtService jwtService;

    public ProviderProfileController(ProviderProfileRepository providerProfileRepository, JwtService jwtService) {
        this.providerProfileRepository = providerProfileRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/profile/me")
    public ResponseEntity<ProviderProfile> getMyProfile(@CookieValue(value = "jwt", required = false) String jwt) {
        if (jwt == null || !jwtService.validateToken(jwt)) {
            return ResponseEntity.badRequest().body(null);
        }
        String email = jwtService.extractUsername(jwt);
        if (email == null) {
            return ResponseEntity.badRequest().body(null);
        }
        // Find user by email
        ProviderProfile profile = providerProfileRepository.findAll().stream()
            .filter(p -> p.getUser() != null && email.equals(p.getUser().getEmail()))
            .findFirst().orElse(null);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    private final ProviderProfileRepository providerProfileRepository;

    // ...existing code...

    @GetMapping("/profile/user/{userId}")
    public ResponseEntity<ProviderProfile> getByUserId(@PathVariable Long userId) {
        return providerProfileRepository.findByUserId(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}