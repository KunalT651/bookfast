package com.bookfast.backend.common.auth.service;

import com.bookfast.backend.common.dto.*;
import com.bookfast.backend.common.model.*;
import com.bookfast.backend.common.repository.*;
import com.bookfast.backend.common.util.PasswordUtil;
import com.bookfast.backend.common.notification.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtService jwtService;
    private final EmailService emailService;
    
    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public AuthService(UserRepository userRepo, RoleRepository roleRepo, JwtService jwtService, EmailService emailService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional
    public AuthResponse registerCustomer(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPassword(PasswordUtil.hash(req.getPassword()));
        user.setCreatedDate(java.time.LocalDate.now());
        user.setIsActive(true);

        // Lookup Role entity by name (e.g., "CUSTOMER")
        Role role = roleRepo.findByNameIgnoreCase(req.getRole());
        if (role == null) throw new RuntimeException("Invalid role");
        user.setRole(role);

        User savedUser = userRepo.save(user);

        // Send welcome email
        try {
            if (savedUser.getEmail() != null && !savedUser.getEmail().isEmpty()) {
                String emailContent = createCustomerWelcomeEmail(savedUser);
                emailService.sendHtmlEmail(
                    savedUser.getEmail(),
                    "Welcome to BookFast!",
                    emailContent
                );
                System.out.println("[AuthService] Welcome email sent to: " + savedUser.getEmail());
            }
        } catch (Exception e) {
            System.err.println("[AuthService] Failed to send welcome email: " + e.getMessage());
            // Don't fail registration if email fails
        }

        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().getName());
        return new AuthResponse(token, savedUser);
    }

    @Transactional
    public AuthResponse registerProvider(RegisterProviderRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPassword(PasswordUtil.hash(req.getPassword()));
        user.setOrganizationName(req.getOrganizationName());
        user.setServiceCategory(req.getServiceCategory());
        user.setCreatedDate(java.time.LocalDate.now());
        user.setIsActive(true);

        // Lookup Role entity by name (e.g., "PROVIDER")
        Role role = roleRepo.findByNameIgnoreCase(req.getRole());
        if (role == null) throw new RuntimeException("Invalid role");
        user.setRole(role);

        User savedUser = userRepo.save(user);

        // No longer creating a separate ProviderProfile, as User entity now handles provider details.

        // Send welcome email
        try {
            if (savedUser.getEmail() != null && !savedUser.getEmail().isEmpty()) {
                String emailContent = createProviderWelcomeEmail(savedUser);
                emailService.sendHtmlEmail(
                    savedUser.getEmail(),
                    "Welcome to BookFast - Provider Account Created!",
                    emailContent
                );
                System.out.println("[AuthService] Welcome email sent to: " + savedUser.getEmail());
            }
        } catch (Exception e) {
            System.err.println("[AuthService] Failed to send welcome email: " + e.getMessage());
            // Don't fail registration if email fails
        }

        String token = jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().getName());
        return new AuthResponse(token, savedUser);
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!PasswordUtil.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());
        return new AuthResponse(token, user);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }
    
    /**
     * Create welcome email for customer registration
     */
    private String createCustomerWelcomeEmail(User user) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #10b981; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
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
                        <p>Thank you for registering with BookFast! We're excited to have you on board.</p>
                        
                        <p>Your account has been successfully created. You can now:</p>
                        <ul>
                            <li>Browse and search for service providers</li>
                            <li>Book appointments with your preferred providers</li>
                            <li>Manage your bookings and appointments</li>
                            <li>Leave reviews and ratings</li>
                        </ul>
                        
                        <div style="text-align: center;">
                            <a href="%s/customer/home" class="button">Get Started</a>
                        </div>
                        
                        <p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>
                        
                        <p>Welcome aboard!</p>
                        <p><strong>The BookFast Team</strong></p>
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
            frontendUrl
        );
    }
    
    /**
     * Create welcome email for provider registration
     */
    private String createProviderWelcomeEmail(User user) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #6366f1; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background-color: #f9fafb; padding: 30px; border: 1px solid #e5e7eb; }
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
                        <p>Thank you for registering as a service provider with BookFast! We're excited to have you on our platform.</p>
                        
                        <p>Your provider account has been successfully created. You can now:</p>
                        <ul>
                            <li>Add your services and resources</li>
                            <li>Set your availability and schedule</li>
                            <li>Manage bookings from customers</li>
                            <li>View analytics and reports</li>
                        </ul>
                        
                        <div style="text-align: center;">
                            <a href="%s/provider/dashboard" class="button">Access Your Dashboard</a>
                        </div>
                        
                        <p>If you have any questions or need assistance, please don't hesitate to contact our support team.</p>
                        
                        <p>Welcome to the BookFast provider community!</p>
                        <p><strong>The BookFast Team</strong></p>
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
            frontendUrl
        );
    }
}