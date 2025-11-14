package com.bookfast.backend.common.service;

import com.bookfast.backend.common.model.PasswordResetToken;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.PasswordResetTokenRepository;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import com.bookfast.backend.common.notification.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {
    
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;
    
    private static final int TOKEN_EXPIRY_HOURS = 1; // Token valid for 1 hour
    private static final int TOKEN_LENGTH = 32; // 32 bytes = 44 characters in Base64
    
    public PasswordResetService(PasswordResetTokenRepository tokenRepository, 
                               UserRepository userRepository,
                               EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    /**
     * Generate a secure random token
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    /**
     * Request password reset - creates token and sends email
     */
    @Transactional
    public void requestPasswordReset(String email) {
        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // For security, don't reveal if email exists or not
            System.out.println("[PasswordReset] Email not found: " + email);
            return; // Silently fail
        }
        
        User user = userOpt.get();
        
        // Delete any existing tokens for this user
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        // Generate new token
        String token = generateSecureToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);
        
        // Save token
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);
        
        // Send email with reset link
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            String emailContent = createPasswordResetEmail(user, resetLink);
            emailService.sendHtmlEmail(
                user.getEmail(),
                "BookFast - Password Reset Request",
                emailContent
            );
            System.out.println("[PasswordReset] Reset email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("[PasswordReset] Failed to send reset email: " + e.getMessage());
            throw new RuntimeException("Failed to send reset email");
        }
    }
    
    /**
     * Create HTML email for password reset
     */
    private String createPasswordResetEmail(User user, String resetLink) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #6366f1; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
                    .button { display: inline-block; background-color: #6366f1; color: white; padding: 15px 40px; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }
                    .button:hover { background-color: #4f46e5; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 0.875rem; }
                    .warning { background-color: #fef3c7; padding: 15px; border-radius: 6px; margin: 20px 0; border-left: 4px solid #f59e0b; }
                    .code-box { background-color: #f3f4f6; padding: 15px; border-radius: 6px; font-family: monospace; word-break: break-all; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>We received a request to reset your BookFast password.</p>
                        
                        <p>Click the button below to reset your password:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">Reset Password</a>
                        </div>
                        
                        <p><small>Or copy and paste this link into your browser:</small></p>
                        <div class="code-box">%s</div>
                        
                        <div class="warning">
                            <strong>⚠️ Important Security Information:</strong>
                            <ul style="margin: 10px 0;">
                                <li>This link will expire in <strong>1 hour</strong></li>
                                <li>This link can only be used once</li>
                                <li>If you didn't request this reset, please ignore this email</li>
                                <li>Your password will not change unless you click the link above</li>
                            </ul>
                        </div>
                        
                        <p>If you didn't request a password reset, someone may have entered your email address by mistake. Your account is secure and no action is needed.</p>
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
    
    /**
     * Validate reset token
     */
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        // Check if token is expired or already used
        return !resetToken.isExpired() && !resetToken.isUsed();
    }
    
    /**
     * Reset password using token
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Invalid reset token");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        // Validate token
        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token has expired");
        }
        
        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }
        
        // Update user password
        User user = resetToken.getUser();
        user.setPassword(PasswordUtil.hash(newPassword));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        System.out.println("[PasswordReset] Password successfully reset for user: " + user.getEmail());
    }
    
    /**
     * Clean up expired tokens (can be called periodically)
     */
    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}

