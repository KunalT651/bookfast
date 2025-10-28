package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminUserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User createUser(User user) {
        // Set default password if not provided
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.hash("defaultPassword123"));
        } else {
            user.setPassword(PasswordUtil.hash(user.getPassword()));
        }

        // Set role if not provided
        if (user.getRole() == null) {
            user.setRole(roleRepository.findByNameIgnoreCase("CUSTOMER"));
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userData) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        
        // Update fields
        if (userData.getFirstName() != null) {
            user.setFirstName(userData.getFirstName());
        }
        if (userData.getLastName() != null) {
            user.setLastName(userData.getLastName());
        }
        if (userData.getEmail() != null) {
            user.setEmail(userData.getEmail());
        }
        if (userData.getOrganizationName() != null) {
            user.setOrganizationName(userData.getOrganizationName());
        }
        if (userData.getServiceCategory() != null) {
            user.setServiceCategory(userData.getServiceCategory());
        }
        if (userData.getRole() != null) {
            user.setRole(userData.getRole());
        }

        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRoleNameIgnoreCase(role);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            query, query, query);
    }
}
