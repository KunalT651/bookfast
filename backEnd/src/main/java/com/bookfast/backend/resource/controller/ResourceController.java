package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.service.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final ResourceService service;

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable Long id) {
        service.deleteResource(id);
    }

    // Filter resources by specialization (service)
    @GetMapping("/filter")
    public List<Resource> filterResources(@RequestParam(required = false) String specialization,
            @RequestParam(required = false) String status) {
        if (specialization != null && status != null) {
            // Filter by both specialization and status
            return service.getResourcesBySpecializationAndStatus(specialization, status);
        } else if (specialization != null) {
            return service.getResourcesBySpecialization(specialization);
        } else if (status != null) {
            return service.getResourcesByStatus(status);
        } else {
            return service.getAllActiveResourcesForCustomers();
        }
    }

    // Customer-facing endpoint: returns all active resources
    @GetMapping
    public List<Resource> getAllResourcesForCustomers() {
        return service.getAllActiveResourcesForCustomers();
    }

    @GetMapping("/{id}")
    public Resource getResource(@PathVariable Long id) {
        return service.getResourceById(id);
    }

    @PostMapping
    public Resource createResource(@RequestBody Resource resource) {
        return service.createResource(resource);
    }

    @PutMapping("/{id}")
    public Resource updateResource(@PathVariable Long id, @RequestBody Resource resource) {
        resource.setId(id);
        return service.updateResource(resource);
    }

    // Provider-facing endpoint: returns all resources for a specific provider
    @GetMapping("/provider/{providerId}")
    public List<Resource> getResourcesByProvider(@PathVariable Long providerId) {
        return service.getResourcesByProvider(providerId);
    }

    // Provider-facing endpoint: returns all resources for the current provider
    // (from authentication context)
    @GetMapping("/me")
    public List<Resource> getResourcesForCurrentProvider(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long providerId = service.getProviderIdFromAuthHeader(authHeader);
        return service.getResourcesByProvider(providerId);
    }
}