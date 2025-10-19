package com.bookfast.backend.common.auth.controller;

import com.bookfast.backend.common.dto.AuthResponse;
import com.bookfast.backend.common.dto.RegisterRequest;
import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public AdminController(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");

            if (email == null || password == null || firstName == null || lastName == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
            }

            if (userRepo.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            // Create admin user
            User admin = new User();
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setEmail(email);
            admin.setPassword(PasswordUtil.hash(password));

            // Get or create ADMIN role
            Role adminRole = roleRepo.findByNameIgnoreCase("ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole = roleRepo.save(adminRole);
            }
            admin.setRole(adminRole);

            userRepo.save(admin);

            return ResponseEntity.ok(Map.of(
                "message", "Admin user created successfully",
                "email", email,
                "role", "ADMIN"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-admin-exists")
    public ResponseEntity<?> checkAdminExists() {
        Role adminRole = roleRepo.findByNameIgnoreCase("ADMIN");
        if (adminRole == null) {
            return ResponseEntity.ok(Map.of("exists", false, "message", "No admin role found"));
        }
        
        long adminCount = userRepo.countByRole(adminRole);
        return ResponseEntity.ok(Map.of(
            "exists", adminCount > 0,
            "count", adminCount,
            "message", adminCount > 0 ? "Admin users exist" : "No admin users found"
        ));
    }
}
