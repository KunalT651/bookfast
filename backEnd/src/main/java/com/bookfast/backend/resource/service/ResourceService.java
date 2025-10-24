package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository repository;
    private final UserRepository userRepository;

    public ResourceService(ResourceRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // Extract providerId from JWT
    public Long getProviderIdFromAuthHeader(String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // Parse JWT token to extract user ID
                // For now, we'll use the SecurityContext to get the current user
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                    org.springframework.security.core.userdetails.UserDetails userDetails = 
                        (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
                    String username = userDetails.getUsername();
                    User user = userRepository.findByEmail(username).orElse(null);
                    return user != null ? user.getId() : 1L;
                }
            }
        } catch (Exception e) {
            // Log the error and fallback to default
            System.err.println("Error parsing JWT token: " + e.getMessage());
        }
        return 1L; // Fallback for demo purposes
    }

    public void deleteResource(Long id) {
        repository.deleteById(id);
    }

    public Resource updateResource(Resource resource) {
        if (resource.getId() == null || !repository.existsById(resource.getId())) {
            throw new IllegalArgumentException("Resource not found for update");
        }
        return repository.save(resource);
    }

    public List<Resource> getResourcesBySpecializationAndStatus(String specialization, String status) {
        // You may need to add a custom query to ResourceRepository for this
        return repository.findBySpecializationAndStatus(specialization, status);
    }

    public List<Resource> getResourcesByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<Resource> getAllResources() {
        return repository.findAll();
    }

    // Return only active resources for customers
    public List<Resource> getAllActiveResourcesForCustomers() {
        return repository.findByStatus("active");
    }

    public Resource getResourceById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Resource> getResourcesBySpecialization(String specialization) {
        return repository.findBySpecialization(specialization);
    }

    public List<Resource> getResourcesByProvider(Long providerId) {
        return repository.findByProviderId(providerId);
    }

    public Resource createResource(Resource resource) {
        return repository.save(resource);
    }
}
