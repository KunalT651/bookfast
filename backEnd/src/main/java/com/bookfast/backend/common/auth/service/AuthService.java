package com.bookfast.backend.common.auth.service;

import com.bookfast.backend.common.dto.*;
import com.bookfast.backend.common.model.*;
import com.bookfast.backend.common.repository.*;
import com.bookfast.backend.common.util.PasswordUtil;
import com.bookfast.backend.common.notification.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtService jwtService;
    private final EmailService emailService;

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

        // Lookup Role entity by name (e.g., "CUSTOMER")
        Role role = roleRepo.findByNameIgnoreCase(req.getRole());
        if (role == null) {
            // Create role if it doesn't exist
            role = new Role();
            role.setName(req.getRole());
            role = roleRepo.save(role);
        }
        user.setRole(role);

        User savedUser = userRepo.save(user);

        // Send welcome email to customer (non-blocking - won't fail registration if email fails)
        if ("CUSTOMER".equalsIgnoreCase(savedUser.getRole().getName())) {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName(), savedUser.getLastName());
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
        
        // Set provider-specific fields
        user.setOrganizationName(req.getOrganizationName());
        user.setServiceCategory(req.getServiceCategory());

        // Lookup Role entity by name (e.g., "PROVIDER")
        Role role = roleRepo.findByNameIgnoreCase(req.getRole());
        if (role == null) {
            // Create role if it doesn't exist
            role = new Role();
            role.setName(req.getRole());
            role = roleRepo.save(role);
        }
        user.setRole(role);

        userRepo.save(user);

        // No longer creating a separate ProviderProfile, as User entity now handles provider details.

        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());
        return new AuthResponse(token, user);
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByEmail(req.email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!PasswordUtil.matches(req.password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());
        return new AuthResponse(token, user);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }
}