package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.resource.repository.ReviewRepository;
import com.bookfast.backend.resource.repository.AvailabilitySlotRepository;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.admin.repository.ServiceCategoryRepository;
import com.bookfast.backend.admin.model.ServiceCategory;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository repository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    public ResourceService(ResourceRepository repository,
                           UserRepository userRepository,
                           ReviewRepository reviewRepository,
                           AvailabilitySlotRepository availabilitySlotRepository,
                           ServiceCategoryRepository serviceCategoryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
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
        List<Resource> resources = repository.findBySpecializationAndStatus(specialization, status);
        // Populate serviceCategory, averageRating, and hasAvailableSlots for each resource
        for (Resource resource : resources) {
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(p.getServiceCategory());
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            // Calculate and set average rating from reviews
            List<com.bookfast.backend.resource.model.Review> reviews = reviewRepository.findByResource(resource);
            if (!reviews.isEmpty()) {
                double avgRating = reviews.stream()
                    .mapToDouble(com.bookfast.backend.resource.model.Review::getRating)
                    .average()
                    .orElse(0.0);
                resource.setRating(avgRating);
            }
            // Check if resource has any available slots
            List<com.bookfast.backend.resource.model.AvailabilitySlot> slots = availabilitySlotRepository.findByResourceId(resource.getId());
            boolean hasAvailable = slots.stream()
                .anyMatch(slot -> slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("available"));
            resource.setHasAvailableSlots(hasAvailable);
        }
        return resources;
    }

    public List<Resource> getResourcesByStatus(String status) {
        List<Resource> resources = repository.findByStatus(status);
        // Populate serviceCategory, averageRating, and hasAvailableSlots for each resource
        for (Resource resource : resources) {
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(p.getServiceCategory());
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            // Calculate and set average rating from reviews
            List<com.bookfast.backend.resource.model.Review> reviews = reviewRepository.findByResource(resource);
            if (!reviews.isEmpty()) {
                double avgRating = reviews.stream()
                    .mapToDouble(com.bookfast.backend.resource.model.Review::getRating)
                    .average()
                    .orElse(0.0);
                resource.setRating(avgRating);
            }
            // Check if resource has any available slots
            List<com.bookfast.backend.resource.model.AvailabilitySlot> slots = availabilitySlotRepository.findByResourceId(resource.getId());
            boolean hasAvailable = slots.stream()
                .anyMatch(slot -> slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("available"));
            resource.setHasAvailableSlots(hasAvailable);
        }
        return resources;
    }

    public List<Resource> getAllResources() {
        return repository.findAll();
    }

    // Return only active resources for customers
    // Returns resources with status "active" or null (default active)
    public List<Resource> getAllActiveResourcesForCustomers() {
        List<Resource> allResources = repository.findAll();
        // Filter to include only active resources or resources with null status (treat as active)
        List<Resource> filteredResources = allResources.stream()
            .filter(resource -> {
                String status = resource.getStatus();
                // Include if status is null, empty, "active", or "available"
                return status == null || 
                       status.isEmpty() || 
                       status.equalsIgnoreCase("active") || 
                       status.equalsIgnoreCase("available");
            })
            .collect(java.util.stream.Collectors.toList());
        
        // Populate serviceCategory, averageRating, and hasAvailableSlots for each resource
        for (Resource resource : filteredResources) {
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(resolveCategoryName(p.getServiceCategory()));
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            // Calculate and set average rating from reviews
            List<com.bookfast.backend.resource.model.Review> reviews = reviewRepository.findByResource(resource);
            if (!reviews.isEmpty()) {
                double avgRating = reviews.stream()
                    .mapToDouble(com.bookfast.backend.resource.model.Review::getRating)
                    .average()
                    .orElse(0.0);
                resource.setRating(avgRating);
            }
            // Check if resource has any available slots
            List<com.bookfast.backend.resource.model.AvailabilitySlot> slots = availabilitySlotRepository.findByResourceId(resource.getId());
            boolean hasAvailable = slots.stream()
                .anyMatch(slot -> slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("available"));
            resource.setHasAvailableSlots(hasAvailable);
        }
        
        return filteredResources;
    }

    public Resource getResourceById(Long id) {
        Resource resource = repository.findById(id).orElse(null);
        if (resource != null) {
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(p.getServiceCategory());
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            // Calculate and set average rating from reviews
            List<com.bookfast.backend.resource.model.Review> reviews = reviewRepository.findByResource(resource);
            if (!reviews.isEmpty()) {
                double avgRating = reviews.stream()
                    .mapToDouble(com.bookfast.backend.resource.model.Review::getRating)
                    .average()
                    .orElse(0.0);
                resource.setRating(avgRating);
            }
            // Check if resource has any available slots
            List<com.bookfast.backend.resource.model.AvailabilitySlot> slots = availabilitySlotRepository.findByResourceId(resource.getId());
            boolean hasAvailable = slots.stream()
                .anyMatch(slot -> slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("available"));
            resource.setHasAvailableSlots(hasAvailable);
        }
        return resource;
    }

    public List<Resource> getResourcesBySpecialization(String specialization) {
        List<Resource> resources = repository.findBySpecialization(specialization);
        // Populate serviceCategory, averageRating, and hasAvailableSlots for each resource
        for (Resource resource : resources) {
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(resolveCategoryName(p.getServiceCategory()));
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(resolveCategoryName(p.getServiceCategory()));
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(resolveCategoryName(p.getServiceCategory()));
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            // Calculate and set average rating from reviews
            List<com.bookfast.backend.resource.model.Review> reviews = reviewRepository.findByResource(resource);
            if (!reviews.isEmpty()) {
                double avgRating = reviews.stream()
                    .mapToDouble(com.bookfast.backend.resource.model.Review::getRating)
                    .average()
                    .orElse(0.0);
                resource.setRating(avgRating);
            }
            // Check if resource has any available slots
            List<com.bookfast.backend.resource.model.AvailabilitySlot> slots = availabilitySlotRepository.findByResourceId(resource.getId());
            boolean hasAvailable = slots.stream()
                .anyMatch(slot -> slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("available"));
            resource.setHasAvailableSlots(hasAvailable);
        }
        return resources;
    }

    public List<Resource> getResourcesByProvider(Long providerId) {
        // Fetch resources
        List<Resource> resources = repository.findByProviderId(providerId);
        // Explicitly set relationships to null to prevent any lazy loading/proxy serialization issues
        // Even though @JsonIgnore should prevent serialization, setting to null is extra safety
        for (Resource resource : resources) {
            if (resource.getProviderId() != null) {
                java.util.Optional<User> provider = userRepository.findById(resource.getProviderId());
                if (provider.isPresent()) {
                    User p = provider.get();
                    resource.setServiceCategory(p.getServiceCategory());
                    String displayName = (
                        (p.getFirstName() != null && !p.getFirstName().isBlank()) ||
                        (p.getLastName() != null && !p.getLastName().isBlank())
                    )
                        ? ( (p.getFirstName() != null ? p.getFirstName() : "") + " " + (p.getLastName() != null ? p.getLastName() : "") ).trim()
                        : (p.getOrganizationName() != null && !p.getOrganizationName().isBlank() ? p.getOrganizationName() : p.getEmail());
                    resource.setProviderName(displayName);
                }
            }
            // Set to null to completely avoid any lazy loading during JSON serialization
            // The @JsonIgnore annotations should already prevent serialization, but this is extra safety
            resource.setReviews(null);
            resource.setAvailabilitySlots(null);
        }
        return resources;
    }

    public Resource createResource(Resource resource) {
        return repository.save(resource);
    }

    /**
     * Resolve a user.stored serviceCategory value into a display name.
     * If the stored value is a numeric id, look up the ServiceCategory table.
     * Otherwise, treat it as an already-resolved name.
     */
    private String resolveCategoryName(String stored) {
        if (stored == null || stored.isBlank()) return null;
        boolean numeric = stored.chars().allMatch(Character::isDigit);
        if (!numeric) {
            return stored;
        }
        try {
            Long id = Long.valueOf(stored);
            java.util.Optional<ServiceCategory> sc = serviceCategoryRepository.findById(id);
            return sc.map(ServiceCategory::getName).orElse(stored);
        } catch (NumberFormatException ex) {
            return stored;
        }
    }
}
