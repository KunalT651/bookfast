package com.bookfast.backend.provider.service;

import com.bookfast.backend.common.dto.ServiceResourceDto;
import com.bookfast.backend.common.model.ProviderProfile;
import com.bookfast.backend.common.repository.ProviderProfileRepository;
import com.bookfast.backend.provider.model.ResourceAvailability;
import com.bookfast.backend.provider.model.ServiceResource;
import com.bookfast.backend.provider.repository.ServiceResourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.List;
import com.bookfast.backend.common.dto.ResourceAvailabilityDto;

@Service
public class ServiceResourceService {
    private final ServiceResourceRepository serviceResourceRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public ServiceResourceService(ServiceResourceRepository serviceResourceRepository,
                                 ProviderProfileRepository providerProfileRepository) {
        this.serviceResourceRepository = serviceResourceRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    public List<ServiceResource> getResourcesByProvider(Long providerId) {
        return serviceResourceRepository.findByProviderId(providerId);
    }

    public Optional<ServiceResource> getById(Long id) {
        return serviceResourceRepository.findById(id);
    }

    public ServiceResource save(ServiceResource resource) {
        return serviceResourceRepository.save(resource);
    }

    public void delete(Long id) {
        serviceResourceRepository.deleteById(id);
    }

public ServiceResource createResourceFromDto(ServiceResourceDto dto) {
    ServiceResource resource = new ServiceResource();
    resource.setName(dto.getName());
    resource.setAddress(dto.getAddress());
    resource.setDescription(dto.getDescription());
    resource.setTags(dto.getTags());
    resource.setPrice(dto.getPrice());
    resource.setExperienceYears(dto.getExperienceYears());
    resource.setPhone(dto.getPhone());
    resource.setEmail(dto.getEmail());
    resource.setImageUrl(dto.getImageUrl());
    resource.setSpecialization(dto.getSpecialization());
    resource.setStatus(dto.getStatus());

        // Convert DTOs to entities
    List<ResourceAvailability> availabilities = null;
    if (dto.getAvailabilities() != null) {
        availabilities = dto.getAvailabilities().stream().map(dtoA -> {
            ResourceAvailability entityA = new ResourceAvailability();
            // Map fields from dtoA to entityA here
            // Example:
            // entityA.setDayOfWeek(dtoA.getDayOfWeek());
            // entityA.setStartTime(dtoA.getStartTime());
            // entityA.setEndTime(dtoA.getEndTime());
            entityA.setServiceResource(resource); // set the parent
            return entityA;
        }).toList();
    }
    resource.setAvailabilities(availabilities);

    // Map providerId to ProviderProfile entity
    ProviderProfile provider = providerProfileRepository.findById(dto.getProviderId())
        .orElseThrow(() -> new RuntimeException("Provider not found"));
    resource.setProvider(provider);

    return serviceResourceRepository.save(resource);
}
}