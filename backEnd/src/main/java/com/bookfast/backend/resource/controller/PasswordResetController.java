package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/auth/password-reset")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class PasswordResetController {

    @PostMapping
    public String requestPasswordReset(@RequestBody PasswordResetRequest request) {
        // Generate a dummy reset link (for demo)
        String resetLink = "https://bookfast/reset?token=dummy-token";
        // Password reset email logic removed due to missing mail dependency
        return "Reset link sent";
    }

    public static class PasswordResetRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
