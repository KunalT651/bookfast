package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.dto.UserCreateRequest;
import com.bookfast.backend.common.dto.UserUpdateRequest;
import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import com.bookfast.backend.common.notification.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final com.bookfast.backend.common.repository.PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;
    
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 12;

    public AdminUserService(UserRepository userRepository, RoleRepository roleRepository, EmailService emailService,
                            com.bookfast.backend.common.repository.PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }
    
    /**
     * Generate a secure random password
     */
    private String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(index));
        }
        
        return password.toString();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User createUser(UserCreateRequest request) {
        User user = new User();
        
        // Set basic fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setOrganizationName(request.getOrganizationName());
        user.setServiceCategory(request.getServiceCategory());
        user.setCreatedDate(LocalDate.now());
        user.setIsActive(true);
        
        // Set role
        String roleName = request.getRole() != null ? request.getRole() : "CUSTOMER";
        Role role = roleRepository.findByNameIgnoreCase(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }
        user.setRole(role);
        
        // Generate or use provided password
        String generatedPassword;
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            generatedPassword = generateSecurePassword();
        } else {
            generatedPassword = request.getPassword();
        }
        user.setPassword(PasswordUtil.hash(generatedPassword));

        // Save user
        User savedUser = userRepository.save(user);

        // Send welcome email based on role
        try {
            if (savedUser.getEmail() != null && !savedUser.getEmail().isEmpty()) {
                System.out.println("[AdminUserService] Attempting to send welcome email to: " + savedUser.getEmail());
                String emailContent;
                String subject;
                
                if ("CUSTOMER".equalsIgnoreCase(roleName)) {
                    emailContent = createCustomerWelcomeEmail(savedUser, generatedPassword);
                    subject = "Welcome to BookFast - Your Account Has Been Created";
                    System.out.println("[AdminUserService] Created customer welcome email");
                } else if ("PROVIDER".equalsIgnoreCase(roleName)) {
                    emailContent = createProviderWelcomeEmail(savedUser, generatedPassword);
                    subject = "Welcome to BookFast - Your Provider Account Credentials";
                    System.out.println("[AdminUserService] Created provider welcome email");
                } else {
                    // For other roles (like ADMIN), send a generic welcome email
                    emailContent = createGenericWelcomeEmail(savedUser, generatedPassword);
                    subject = "Welcome to BookFast - Your Account Has Been Created";
                    System.out.println("[AdminUserService] Created generic welcome email");
                }
                
                System.out.println("[AdminUserService] Calling emailService.sendHtmlEmail...");
                emailService.sendHtmlEmail(savedUser.getEmail(), subject, emailContent);
                System.out.println("[AdminUserService] SUCCESS: Welcome email sent to: " + savedUser.getEmail());
            } else {
                System.err.println("[AdminUserService] Cannot send email: email is null or empty");
            }
        } catch (Exception e) {
            System.err.println("[AdminUserService] ERROR: Failed to send welcome email to " + savedUser.getEmail());
            System.err.println("[AdminUserService] Exception type: " + e.getClass().getName());
            System.err.println("[AdminUserService] Exception message: " + e.getMessage());
            System.err.println("[AdminUserService] Exception cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
            e.printStackTrace();
            // Don't fail user creation if email fails
        }

        return savedUser;
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        
        // Update fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getOrganizationName() != null) {
            user.setOrganizationName(request.getOrganizationName());
        }
        if (request.getServiceCategory() != null) {
            user.setServiceCategory(request.getServiceCategory());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        
        // Update role if provided
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            Role role = roleRepository.findByNameIgnoreCase(request.getRole());
            if (role == null) {
                throw new RuntimeException("Role not found: " + request.getRole());
            }
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        return userRepository.findById(id).map(user -> {
            // Clean up dependent rows that reference this user to prevent FK violations
            try {
                passwordResetTokenRepository.deleteByUser(user);
            } catch (Exception ignored) {
                // If no tokens exist, proceed with deletion
            }
            userRepository.deleteById(id);
            return true;
        }).orElse(false);
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRoleNameIgnoreCase(role);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            query, query, query);
    }
    
    /**
     * Create welcome email for customer created by admin
     */
    private String createCustomerWelcomeEmail(User user, String password) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #10b981; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
                    .credentials { background-color: white; padding: 20px; margin: 20px 0; border-left: 4px solid #10b981; border-radius: 4px; }
                    .credential-item { margin: 10px 0; }
                    .credential-label { font-weight: bold; color: #10b981; }
                    .credential-value { font-family: monospace; background-color: #f3f4f6; padding: 5px 10px; border-radius: 4px; display: inline-block; margin-top: 5px; }
                    .button { display: inline-block; background-color: #10b981; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 0.875rem; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to BookFast!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Your BookFast account has been successfully created by an administrator.</p>
                        
                        <div class="credentials">
                            <h3>📧 Your Login Credentials:</h3>
                            <div class="credential-item">
                                <div class="credential-label">Email:</div>
                                <div class="credential-value">%s</div>
                            </div>
                            <div class="credential-item">
                                <div class="credential-label">Password:</div>
                                <div class="credential-value">%s</div>
                            </div>
                        </div>
                        
                        <p><strong>⚠️ Important:</strong> Please change your password after your first login for security purposes.</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/login" class="button">Login to Your Account</a>
                        </div>
                        
                        <h3>🚀 What You Can Do:</h3>
                        <ul>
                            <li>Browse and search for service providers</li>
                            <li>Book appointments with your preferred providers</li>
                            <li>Manage your bookings and appointments</li>
                            <li>Leave reviews and ratings</li>
                        </ul>
                        
                        <p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>
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
            user.getEmail(),
            password,
            frontendUrl
        );
    }
    
    /**
     * Create welcome email for provider created by admin
     */
    private String createProviderWelcomeEmail(User user, String password) {
        String organizationName = user.getOrganizationName() != null ? user.getOrganizationName() : "your business";
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #6366f1; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
                    .credentials { background-color: white; padding: 20px; margin: 20px 0; border-left: 4px solid #6366f1; border-radius: 4px; }
                    .credential-item { margin: 10px 0; }
                    .credential-label { font-weight: bold; color: #6366f1; }
                    .credential-value { font-family: monospace; background-color: #f3f4f6; padding: 5px 10px; border-radius: 4px; display: inline-block; margin-top: 5px; }
                    .button { display: inline-block; background-color: #6366f1; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 0.875rem; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to BookFast!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Great news! BookFast has successfully registered <strong>%s</strong> as a service provider on our booking platform.</p>
                        
                        <p>You can now offer your services to customers and manage your bookings through our platform.</p>
                        
                        <div class="credentials">
                            <h3>📧 Your Login Credentials:</h3>
                            <div class="credential-item">
                                <div class="credential-label">Email:</div>
                                <div class="credential-value">%s</div>
                            </div>
                            <div class="credential-item">
                                <div class="credential-label">Password:</div>
                                <div class="credential-value">%s</div>
                            </div>
                        </div>
                        
                        <p><strong>⚠️ Important:</strong> Please change your password after your first login for security purposes.</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/login" class="button">Login to Your Account</a>
                        </div>
                        
                        <h3>🚀 Next Steps:</h3>
                        <ol>
                            <li>Login to your provider account</li>
                            <li>Complete your profile information</li>
                            <li>Add your services and availability</li>
                            <li>Start receiving bookings!</li>
                        </ol>
                        
                        <p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>
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
            organizationName,
            user.getEmail(),
            password,
            frontendUrl
        );
    }
    
    /**
     * Create generic welcome email for other roles
     */
    private String createGenericWelcomeEmail(User user, String password) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #6366f1; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
                    .credentials { background-color: white; padding: 20px; margin: 20px 0; border-left: 4px solid #6366f1; border-radius: 4px; }
                    .credential-item { margin: 10px 0; }
                    .credential-label { font-weight: bold; color: #6366f1; }
                    .credential-value { font-family: monospace; background-color: #f3f4f6; padding: 5px 10px; border-radius: 4px; display: inline-block; margin-top: 5px; }
                    .button { display: inline-block; background-color: #6366f1; color: white; padding: 12px 30px; text-decoration: none; border-radius: 6px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 0.875rem; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Welcome to BookFast!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Your BookFast account has been successfully created by an administrator.</p>
                        
                        <div class="credentials">
                            <h3>📧 Your Login Credentials:</h3>
                            <div class="credential-item">
                                <div class="credential-label">Email:</div>
                                <div class="credential-value">%s</div>
                            </div>
                            <div class="credential-item">
                                <div class="credential-label">Password:</div>
                                <div class="credential-value">%s</div>
                            </div>
                        </div>
                        
                        <p><strong>⚠️ Important:</strong> Please change your password after your first login for security purposes.</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/login" class="button">Login to Your Account</a>
                        </div>
                        
                        <p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>
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
            user.getEmail(),
            password,
            frontendUrl
        );
    }
}
