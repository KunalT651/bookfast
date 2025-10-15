package com.bookfast.backend.provider.service;

import com.bookfast.backend.provider.model.ServiceResource;
import com.bookfast.backend.provider.repository.ServiceResourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceResourceService {
    private final ServiceResourceRepository repository;

    public ServiceResourceService(ServiceResourceRepository repository) {
        this.repository = repository;
    }

    public List<ServiceResource> getResourcesByProvider(Long providerId) {
        return repository.findByProviderId(providerId);
    }

    public Optional<ServiceResource> getById(Long id) {
        return repository.findById(id);
    }

    public ServiceResource save(ServiceResource resource) {
        return repository.save(resource);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}