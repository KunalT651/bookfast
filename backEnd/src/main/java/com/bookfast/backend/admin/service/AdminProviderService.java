package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import com.bookfast.backend.common.notification.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminProviderService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url:https://bookfast-q319.vercel.app}")
    private String frontendUrl;

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 12;

    public AdminProviderService(UserRepository userRepository, RoleRepository roleRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
    }

    public List<User> getAllProviders() {
        return userRepository.findByRoleNameIgnoreCase("PROVIDER");
    }

    public User getProviderById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent() && "PROVIDER".equals(user.get().getRole().getName())) {
            return user.get();
        }
        return null;
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

    /**
     * Create HTML email content for provider welcome email
     */
    private String createProviderWelcomeEmail(User provider, String generatedPassword) {
        String organizationName = provider.getOrganizationName() != null ? provider.getOrganizationName() : "your business";
        
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
                                <div class="credential-label">Temporary Password:</div>
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
            provider.getFirstName(),
            organizationName,
            provider.getEmail(),
            generatedPassword,
            frontendUrl
        );
    }

    @Transactional
    public User createProvider(User provider) {
        // Check if email already exists
        if (userRepository.existsByEmail(provider.getEmail())) {
            throw new RuntimeException("A user with this email already exists");
        }

        // Set PROVIDER role
        provider.setRole(roleRepository.findByNameIgnoreCase("PROVIDER"));
        
        // Generate secure password
        String generatedPassword = generateSecurePassword();
        provider.setPassword(PasswordUtil.hash(generatedPassword));
        
        // Set additional fields
        provider.setCreatedDate(LocalDate.now());
        provider.setIsActive(true);

        // Save provider
        User savedProvider = userRepository.save(provider);

        // Send welcome email with credentials
        try {
            String emailContent = createProviderWelcomeEmail(savedProvider, generatedPassword);
            emailService.sendHtmlEmail(
                savedProvider.getEmail(),
                "Welcome to BookFast - Your Provider Account Credentials",
                emailContent
            );
            System.out.println("[AdminProviderService] Welcome email sent to: " + savedProvider.getEmail());
        } catch (Exception e) {
            System.err.println("[AdminProviderService] Failed to send welcome email: " + e.getMessage());
            // Don't fail the provider creation if email fails
        }

        return savedProvider;
    }

    @Transactional
    public User updateProvider(Long id, User providerData) {
        Optional<User> providerOpt = userRepository.findById(id);
        if (providerOpt.isEmpty() || !"PROVIDER".equals(providerOpt.get().getRole().getName())) {
            return null;
        }

        User provider = providerOpt.get();
        
        // Update fields
        if (providerData.getFirstName() != null) {
            provider.setFirstName(providerData.getFirstName());
        }
        if (providerData.getLastName() != null) {
            provider.setLastName(providerData.getLastName());
        }
        if (providerData.getEmail() != null) {
            provider.setEmail(providerData.getEmail());
        }
        if (providerData.getOrganizationName() != null) {
            provider.setOrganizationName(providerData.getOrganizationName());
        }
        if (providerData.getServiceCategory() != null) {
            provider.setServiceCategory(providerData.getServiceCategory());
        }

        return userRepository.save(provider);
    }

    @Transactional
    public boolean deleteProvider(Long id) {
        Optional<User> provider = userRepository.findById(id);
        if (provider.isPresent() && "PROVIDER".equals(provider.get().getRole().getName())) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateProviderStatus(Long id, boolean isActive) {
        Optional<User> providerOpt = userRepository.findById(id);
        if (providerOpt.isEmpty() || !"PROVIDER".equals(providerOpt.get().getRole().getName())) {
            return false;
        }

        User provider = providerOpt.get();
        provider.setIsActive(isActive);
        userRepository.save(provider);
        return true;
    }

    public List<User> getProvidersByCategory(String category) {
        return userRepository.findByRoleNameIgnoreCaseAndServiceCategoryIgnoreCase("PROVIDER", category);
    }

    public List<User> searchProviders(String query) {
        return userRepository.findByRoleNameIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            "PROVIDER", query, query, query);
    }

    public Map<String, Object> getProviderStats() {
        Map<String, Object> stats = new HashMap<>();
        List<User> allProviders = userRepository.findByRoleNameIgnoreCase("PROVIDER");
        long activeProviders = allProviders.stream().filter(User::getIsActive).count();
        
        stats.put("totalProviders", allProviders.size());
        stats.put("activeProviders", activeProviders);
        return stats;
    }
}
