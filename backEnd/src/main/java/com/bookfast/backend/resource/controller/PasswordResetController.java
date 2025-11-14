package com.bookfast.backend.resource.controller;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.model.PasswordResetToken;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.repository.PasswordResetTokenRepository;
import com.bookfast.backend.common.notification.EmailService;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth/password-reset")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;
    
    private static final int TOKEN_EXPIRY_HOURS = 1; // Token valid for 1 hour

    public PasswordResetController(UserRepository userRepository, 
                                  PasswordResetTokenRepository tokenRepository,
                                  EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    /**
     * Request password reset - generates token and sends email
     */
    @PostMapping("/request")
    @Transactional
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        try {
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                // Don't reveal if email exists or not (security best practice)
                return ResponseEntity.ok(new MessageResponse("If an account with that email exists, a password reset link has been sent."));
            }
            
            User user = userOpt.get();
            
            // Delete any existing tokens for this user
            tokenRepository.deleteByUser(user);
            
            // Generate secure random token
            String token = generateSecureToken();
            
            // Create and save token
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));
            resetToken.setUsed(false);
            tokenRepository.save(resetToken);
            
            // Send email with reset link
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String emailContent = createPasswordResetEmail(user, resetLink);
            
            emailService.sendHtmlEmail(
                user.getEmail(),
                "BookFast - Password Reset Request",
                emailContent
            );
            
            System.out.println("[PasswordReset] Reset email sent to: " + user.getEmail());
            
            return ResponseEntity.ok(new MessageResponse("If an account with that email exists, a password reset link has been sent."));
            
        } catch (Exception e) {
            System.err.println("[PasswordReset] Error: " + e.getMessage());
            return ResponseEntity.ok(new MessageResponse("If an account with that email exists, a password reset link has been sent."));
        }
    }
    
    /**
     * Verify token validity
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired reset token."));
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.getUsed()) {
            return ResponseEntity.badRequest().body(new MessageResponse("This reset link has already been used."));
        }
        
        if (resetToken.isExpired()) {
            return ResponseEntity.badRequest().body(new MessageResponse("This reset link has expired. Please request a new one."));
        }
        
        return ResponseEntity.ok(new MessageResponse("Token is valid."));
    }
    
    /**
     * Reset password using token
     */
    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            // Validate passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Passwords do not match."));
            }
            
            // Validate password strength (optional)
            if (request.getNewPassword().length() < 8) {
                return ResponseEntity.badRequest().body(new MessageResponse("Password must be at least 8 characters long."));
            }
            
            // Find token
            Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(request.getToken());
            
            if (tokenOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired reset token."));
            }
            
            PasswordResetToken resetToken = tokenOpt.get();
            
            // Check if token is used
            if (resetToken.getUsed()) {
                return ResponseEntity.badRequest().body(new MessageResponse("This reset link has already been used."));
            }
            
            // Check if token is expired
            if (resetToken.isExpired()) {
                return ResponseEntity.badRequest().body(new MessageResponse("This reset link has expired. Please request a new one."));
            }
            
            // Update user password
            User user = resetToken.getUser();
            user.setPassword(PasswordUtil.hash(request.getNewPassword()));
            userRepository.save(user);
            
            // Mark token as used
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
            
            System.out.println("[PasswordReset] Password reset successful for: " + user.getEmail());
            
            return ResponseEntity.ok(new MessageResponse("Password has been reset successfully! You can now login with your new password."));
            
        } catch (Exception e) {
            System.err.println("[PasswordReset] Error resetting password: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to reset password. Please try again."));
        }
    }
    
    /**
     * Generate a secure random token
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32]; // 256 bits
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * Create HTML email content for password reset
     */
    private String createPasswordResetEmail(User user, String resetLink) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #ef4444; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
                    .button { display: inline-block; background-color: #ef4444; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 0.875rem; }
                    .warning { background-color: #fef3c7; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #f59e0b; }
                    .code { background-color: #f3f4f6; padding: 10px; border-radius: 4px; font-family: monospace; word-break: break-all; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔒 Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>We received a request to reset the password for your BookFast account.</p>
                        
                        <p>Click the button below to reset your password:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">Reset My Password</a>
                        </div>
                        
                        <p>Or copy and paste this link into your browser:</p>
                        <div class="code">%s</div>
                        
                        <div class="warning">
                            <strong>⏰ Important:</strong> This link will expire in 1 hour for security reasons.
                        </div>
                        
                        <p><strong>Didn't request this?</strong> If you didn't request a password reset, please ignore this email. Your password will remain unchanged.</p>
                        
                        <p>For security reasons, never share this link with anyone.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 BookFast. All rights reserved.</p>
                        <p>This is an automated message. Please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            user.getFirstName(),
            resetLink,
            resetLink
        );
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
        private String confirmPassword;

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

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
    
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
