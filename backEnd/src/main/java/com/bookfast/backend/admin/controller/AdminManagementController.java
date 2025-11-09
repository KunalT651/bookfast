package com.bookfast.backend.admin.controller;

import com.bookfast.backend.common.service.AdminInitializationService;
import com.bookfast.backend.common.model.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/management")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class AdminManagementController {
    
    private final AdminInitializationService adminInitService;

    public AdminManagementController(AdminInitializationService adminInitService) {
        this.adminInitService = adminInitService;
    }

    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, Object>> createAdminUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String firstName = request.get("firstName");
            String lastName = request.get("lastName");

            if (email == null || password == null || firstName == null || lastName == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
            }

            User admin = adminInitService.createAdminUser(email, password, firstName, lastName);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin user created successfully");
            response.put("email", admin.getEmail());
            response.put("name", admin.getFirstName() + " " + admin.getLastName());
            response.put("role", "ADMIN");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/initialize-default-admin")
    public ResponseEntity<Map<String, Object>> initializeDefaultAdmin() {
        try {
            adminInitService.createDefaultAdminIfNotExists();
            return ResponseEntity.ok(Map.of("message", "Default admin initialization completed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
