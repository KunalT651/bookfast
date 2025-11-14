package com.bookfast.backend.common.auth.service;

import com.bookfast.backend.common.dto.*;
import com.bookfast.backend.common.model.*;
import com.bookfast.backend.common.repository.*;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo, RoleRepository roleRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.jwtService = jwtService;
    }

    @Transactional
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
        user.setPassword(PasswordUtil.hash(req.password));

        // Lookup Role entity by name (e.g., "CUSTOMER")
        Role role = roleRepo.findByNameIgnoreCase(req.role);
        if (role == null) throw new RuntimeException("Invalid role");
        user.setRole(role);

        userRepo.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole().getName());
        return new AuthResponse(token, user);
    }

    @Transactional
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
        user.setPassword(PasswordUtil.hash(req.password));

        // Lookup Role entity by name (e.g., "PROVIDER")
        Role role = roleRepo.findByNameIgnoreCase(req.getRole());
        if (role == null) throw new RuntimeException("Invalid role");
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