package com.bookfast.backend.common.service;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AdminInitializationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${admin.default.email:admin@bookfast.com}")
    private String adminEmail;

    @Value("${admin.default.password:admin123}")
    private String adminPassword;

    @Value("${admin.default.firstname:System}")
    private String adminFirstName;

    @Value("${admin.default.lastname:Administrator}")
    private String adminLastName;

    @Value("${admin.auto-create:true}")
    private boolean autoCreateAdmin;

    public AdminInitializationService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Bean
    public ApplicationRunner initializeAdmin() {
        return args -> {
            if (autoCreateAdmin) {
                createDefaultAdminIfNotExists();
            }
        };
    }

    @Transactional
    public void createDefaultAdminIfNotExists() {
        try {
            // Check if admin already exists
            if (userRepository.findByEmail(adminEmail).isPresent()) {
                System.out.println("[AdminInit] Admin user already exists: " + adminEmail);
                return;
            }

            // Create or get ADMIN role
            Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole = roleRepository.save(adminRole);
                System.out.println("[AdminInit] Created ADMIN role");
            }

            // Create admin user
            User admin = new User();
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setEmail(adminEmail);
            admin.setPassword(PasswordUtil.hash(adminPassword));
            admin.setRole(adminRole);
            admin.setCreatedDate(LocalDate.now());

            userRepository.save(admin);

            System.out.println("========================================");
            System.out.println("🔐 ADMIN USER CREATED SUCCESSFULLY!");
            System.out.println("📧 Email: " + adminEmail);
            System.out.println("🔑 Password: " + adminPassword);
            System.out.println("🌐 Login URL: http://localhost:4200/login");
            System.out.println("📊 Dashboard: http://localhost:4200/admin/dashboard");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("[AdminInit] Error creating admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to create additional admin users programmatically
    @Transactional
    public User createAdminUser(String email, String password, String firstName, String lastName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole = roleRepository.save(adminRole);
        }

        User admin = new User();
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setPassword(PasswordUtil.hash(password));
        admin.setRole(adminRole);
        admin.setCreatedDate(LocalDate.now());

        return userRepository.save(admin);
    }
}
