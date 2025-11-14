package com.bookfast.backend.resource.controller;

import com.bookfast.backend.common.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Request password reset - sends email with reset link
     */
    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link has been sent"));
        } catch (Exception e) {
            System.err.println("[PasswordResetController] Error: " + e.getMessage());
            // For security, always return success message
            return ResponseEntity.ok(Map.of("message", "If the email exists, a reset link has been sent"));
        }
    }

    /**
     * Validate reset token
     */
    @GetMapping("/password-reset/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(Map.of("valid", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Invalid or expired token"));
        }
    }

    /**
     * Reset password using token
     */
    @PostMapping("/password-reset/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // DTOs
    public static class PasswordResetRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
