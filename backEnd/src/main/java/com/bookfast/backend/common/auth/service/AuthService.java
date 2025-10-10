package com.bookfast.backend.common.auth.service;

import com.bookfast.backend.common.dto.*;
import com.bookfast.backend.common.model.*;
import com.bookfast.backend.common.repository.*;
import com.bookfast.backend.common.util.PasswordUtil;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final ProviderProfileRepository providerRepo;
private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

public AuthService(UserRepository userRepo, ProviderProfileRepository providerRepo) {
    this.userRepo = userRepo;
    this.providerRepo = providerRepo;
}

public AuthResponse registerCustomer(RegisterRequest req) {
    if (!req.password.equals(req.confirmPassword)) {
        throw new RuntimeException("Passwords do not match");
    }
    if (userRepo.existsByEmail(req.email)) {
        throw new RuntimeException("Email already exists");
    }
    User user = new User();
    user.setFirstName(req.firstName);
    user.setLastName(req.lastName);
    user.setEmail(req.email);
    user.setPassword(req.password); // Store as plain text for now
    user.setRole(Role.CUSTOMER);
    userRepo.save(user);

    AuthResponse res = new AuthResponse();
    res.email = user.getEmail();
    res.role = user.getRole().name();
    res.token = "";
    return res;
}

public AuthResponse registerProvider(RegisterProviderRequest req) {
    if (!req.password.equals(req.confirmPassword)) {
        throw new RuntimeException("Passwords do not match");
    }
    if (userRepo.existsByEmail(req.email)) {
        throw new RuntimeException("Email already exists");
    }
    User user = new User();
    user.setFirstName(req.firstName);
    user.setLastName(req.lastName);
    user.setEmail(req.email);
    user.setPassword(PasswordUtil.hash(req.password)); // Use hashing if set up
    user.setRole(Role.PROVIDER);
    userRepo.save(user);

    ProviderProfile profile = new ProviderProfile();
    profile.setOrganizationName(req.organizationName);
    profile.setServiceCategory(req.serviceCategory);
    profile.setUser(user);
    providerRepo.save(profile);

    AuthResponse res = new AuthResponse();
    res.email = user.getEmail();
    res.role = user.getRole().name();
    res.token = ""; // Add JWT if needed
    return res;
}

// ...do the same for registerProvider and login...
    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByEmail(req.email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!encoder.matches(req.password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        AuthResponse res = new AuthResponse();
        res.email = user.getEmail();
        res.role = user.getRole().name();
        res.token = ""; // JWT token logic can be added here
        return res;
    }

    
}