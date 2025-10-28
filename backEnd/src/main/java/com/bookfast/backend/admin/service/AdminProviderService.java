package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.model.User;
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
        // Set PROVIDER role
        provider.setRole(roleRepository.findByNameIgnoreCase("PROVIDER"));
        
        // Set default password if not provided
        if (provider.getPassword() == null || provider.getPassword().isEmpty()) {
            provider.setPassword(PasswordUtil.hash("defaultPassword123"));
        } else {
            provider.setPassword(PasswordUtil.hash(provider.getPassword()));
        }

        return userRepository.save(provider);
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
