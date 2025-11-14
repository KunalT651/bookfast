package com.bookfast.backend.admin.service;

import com.bookfast.backend.common.dto.UserCreateRequest;
import com.bookfast.backend.common.dto.UserUpdateRequest;
import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.repository.RoleRepository;
import com.bookfast.backend.common.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    public User createUser(UserCreateRequest request) {
        User user = new User();
        
        // Set basic fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setOrganizationName(request.getOrganizationName());
        user.setServiceCategory(request.getServiceCategory());
        user.setCreatedDate(LocalDate.now());
        user.setIsActive(true);
        
        // Set password
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.hash("defaultPassword123"));
        } else {
            user.setPassword(PasswordUtil.hash(request.getPassword()));
        }

        // Set role
        String roleName = request.getRole() != null ? request.getRole() : "CUSTOMER";
        Role role = roleRepository.findByNameIgnoreCase(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }
        user.setRole(role);

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        
        // Update fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getOrganizationName() != null) {
            user.setOrganizationName(request.getOrganizationName());
        }
        if (request.getServiceCategory() != null) {
            user.setServiceCategory(request.getServiceCategory());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        
        // Update role if provided
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            Role role = roleRepository.findByNameIgnoreCase(request.getRole());
            if (role == null) {
                throw new RuntimeException("Role not found: " + request.getRole());
            }
            user.setRole(role);
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
