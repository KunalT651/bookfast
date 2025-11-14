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

    // Extract providerId from JWT (stub for demo)
    public Long getProviderIdFromAuthHeader(String authHeader) {
        // TODO: Parse JWT and extract providerId
        // For demo, return a fixed value or parse from token
        return 1L;
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
    // Returns resources with status "active" or null (default active)
    public List<Resource> getAllActiveResourcesForCustomers() {
        List<Resource> allResources = repository.findAll();
        // Filter to include only active resources or resources with null status (treat as active)
        return allResources.stream()
            .filter(resource -> {
                String status = resource.getStatus();
                // Include if status is null, empty, "active", or "available"
                return status == null || 
                       status.isEmpty() || 
                       status.equalsIgnoreCase("active") || 
                       status.equalsIgnoreCase("available");
            })
            .collect(java.util.stream.Collectors.toList());
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
