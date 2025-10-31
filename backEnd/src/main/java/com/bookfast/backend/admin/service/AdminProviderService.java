package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminProviderService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminProviderService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

    @Transactional
    public User createProvider(User provider) {
        // Find and set PROVIDER role - throw exception if not found
        Role providerRole = roleRepository.findByNameIgnoreCase("PROVIDER");
        if (providerRole == null) {
            throw new RuntimeException("PROVIDER role not found in database. Please ensure roles are initialized.");
        }
        
        // Create a new User object to avoid any JSON deserialization issues with role
        User newProvider = new User();
        newProvider.setFirstName(provider.getFirstName());
        newProvider.setLastName(provider.getLastName());
        newProvider.setEmail(provider.getEmail());
        newProvider.setOrganizationName(provider.getOrganizationName());
        newProvider.setServiceCategory(provider.getServiceCategory());
        newProvider.setIsActive(provider.getIsActive() != null ? provider.getIsActive() : true);
        
        // Explicitly set the PROVIDER role (ensure it's a managed entity)
        // Fetch the role to ensure it's a managed entity in the current persistence context
        Role managedRole = roleRepository.findById(providerRole.getId())
            .orElseThrow(() -> new RuntimeException("PROVIDER role not found with ID: " + providerRole.getId()));
        newProvider.setRole(managedRole);
        
        // Require password - throw exception if not provided
        if (provider.getPassword() == null || provider.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required when creating a provider");
        }
        
        // Hash the password
        newProvider.setPassword(PasswordUtil.hash(provider.getPassword()));
        
        // Set created date
        newProvider.setCreatedDate(java.time.LocalDate.now());

        // Save the provider with role
        User savedProvider = userRepository.save(newProvider);
        
        // The role should be persisted with the user due to @ManyToOne relationship
        // Verify role was set before returning
        if (savedProvider.getRole() == null || savedProvider.getRole().getId() == null) {
            // If role is not set, fetch it explicitly
            savedProvider.setRole(managedRole);
            savedProvider = userRepository.save(savedProvider);
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
