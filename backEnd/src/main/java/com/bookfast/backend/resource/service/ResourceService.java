package com.bookfast.backend.resource.service;

import org.springframework.stereotype.Service;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.ResourceRepository;

import java.util.List;

@Service
public class ResourceService {
    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
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