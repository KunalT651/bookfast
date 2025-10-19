package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.service.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-resource")
public class TestResourceController {
    private final ResourceService resourceService;

    public TestResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/create-test-resource")
    public ResponseEntity<?> createTestResource() {
        try {
            // Create a test resource directly in the resource table
            Resource testResource = new Resource();
            testResource.setProviderId(1L);
            testResource.setName("Test Doctor");
            testResource.setDescription("This is a test resource to verify the unified system");
            testResource.setSpecialization("General Medicine");
            testResource.setStatus("active");
            testResource.setPrice(100.0);
            testResource.setExperienceYears(5);
            testResource.setPhone("555-0123");
            testResource.setEmail("test@doctor.com");
            testResource.setImageUrl("https://via.placeholder.com/300x200?text=Test+Doctor");
            testResource.setTags(Arrays.asList("Test", "General", "Medicine"));
            testResource.setRating(4.5);

            Resource saved = resourceService.createResource(testResource);

            return ResponseEntity.ok(Map.of(
                "message", "Test resource created successfully in resource table",
                "resource_id", saved.getId(),
                "resource_name", saved.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-resources")
    public ResponseEntity<?> checkResources() {
        try {
            List<Resource> allResources = resourceService.getAllResources();
            List<Resource> activeResources = resourceService.getAllActiveResourcesForCustomers();
            
            return ResponseEntity.ok(Map.of(
                "total_resources", allResources.size(),
                "active_resources", activeResources.size(),
                "all_resources", allResources,
                "active_resources_list", activeResources
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
