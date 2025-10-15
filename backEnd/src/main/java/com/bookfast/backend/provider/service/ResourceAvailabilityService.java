package com.bookfast.backend.provider.service;

import com.bookfast.backend.provider.model.ResourceAvailability;
import com.bookfast.backend.provider.repository.ResourceAvailabilityRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceAvailabilityService {
    private final ResourceAvailabilityRepository repository;

    public ResourceAvailabilityService(ResourceAvailabilityRepository repository) {
        this.repository = repository;
    }

    public List<ResourceAvailability> getByResource(Long resourceId) {
        return repository.findByResourceId(resourceId);
    }

    public Optional<ResourceAvailability> getById(Long id) {
        return repository.findById(id);
    }

    public ResourceAvailability save(ResourceAvailability availability) {
        return repository.save(availability);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}