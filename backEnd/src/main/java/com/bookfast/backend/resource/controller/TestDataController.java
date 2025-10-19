package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestDataController {
    private final ResourceService resourceService;

    public TestDataController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/create-sample-resources")
    public ResponseEntity<?> createSampleResources() {
        try {
            // Create sample resources for testing
            List<Resource> sampleResources = Arrays.asList(
                createSampleResource(1L, "Dr. Sarah Johnson", "Cardiologist", "Expert in heart conditions and preventive care", 150.0, 10, "555-0101", "sarah.johnson@hospital.com", "https://via.placeholder.com/300x200?text=Dr.+Sarah+Johnson", Arrays.asList("Cardiology", "Heart Health", "Preventive Care")),
                createSampleResource(2L, "Dr. Michael Chen", "Dermatologist", "Specialized in skin conditions and cosmetic procedures", 120.0, 8, "555-0102", "michael.chen@clinic.com", "https://via.placeholder.com/300x200?text=Dr.+Michael+Chen", Arrays.asList("Dermatology", "Skin Care", "Cosmetic")),
                createSampleResource(3L, "Dr. Emily Rodriguez", "Pediatrician", "Caring for children from birth to adolescence", 100.0, 12, "555-0103", "emily.rodriguez@pediatrics.com", "https://via.placeholder.com/300x200?text=Dr.+Emily+Rodriguez", Arrays.asList("Pediatrics", "Child Health", "Family Care")),
                createSampleResource(4L, "Dr. James Wilson", "Orthopedic Surgeon", "Expert in bone and joint surgery", 200.0, 15, "555-0104", "james.wilson@surgery.com", "https://via.placeholder.com/300x200?text=Dr.+James+Wilson", Arrays.asList("Orthopedics", "Surgery", "Sports Medicine")),
                createSampleResource(5L, "Dr. Lisa Park", "Psychiatrist", "Mental health and behavioral therapy specialist", 130.0, 9, "555-0105", "lisa.park@mentalhealth.com", "https://via.placeholder.com/300x200?text=Dr.+Lisa+Park", Arrays.asList("Psychiatry", "Mental Health", "Therapy"))
            );

            for (Resource resource : sampleResources) {
                resourceService.createResource(resource);
            }

            return ResponseEntity.ok(Map.of(
                "message", "Sample resources created successfully",
                "count", sampleResources.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Resource createSampleResource(Long providerId, String name, String specialization, 
                                        String description, Double price, Integer experienceYears,
                                        String phone, String email, String imageUrl, List<String> tags) {
        Resource resource = new Resource();
        resource.setProviderId(providerId);
        resource.setName(name);
        resource.setSpecialization(specialization);
        resource.setDescription(description);
        resource.setPrice(price);
        resource.setExperienceYears(experienceYears);
        resource.setPhone(phone);
        resource.setEmail(email);
        resource.setImageUrl(imageUrl);
        resource.setTags(tags);
        resource.setStatus("active");
        resource.setRating(4.5); // Sample rating
        return resource;
    }
}
