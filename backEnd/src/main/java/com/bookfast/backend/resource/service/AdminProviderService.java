package com.bookfast.backend.resource.service;

import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Role providerRole = roleRepository.findByNameIgnoreCase("PROVIDER");
        if (providerRole != null) {
            return userRepository.findByRole(providerRole);
        }
        return List.of();
    }

    @Transactional
    public User updateProvider(Long id, User updated) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Ensure the user is a provider before updating provider-specific fields
            if (!"PROVIDER".equals(user.getRole().getName())) {
                throw new RuntimeException("User is not a provider.");
            }
            user.setFirstName(updated.getFirstName());
            user.setLastName(updated.getLastName());
            user.setEmail(updated.getEmail());
            user.setOrganizationName(updated.getOrganizationName());
            user.setServiceCategory(updated.getServiceCategory());
            // Password is not updated here, should be a separate process
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteProvider(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Ensure the user is a provider before deleting
            if (!"PROVIDER".equals(user.getRole().getName())) {
                throw new RuntimeException("User is not a provider.");
            }
            userRepository.deleteById(id);
        }
    }
}
