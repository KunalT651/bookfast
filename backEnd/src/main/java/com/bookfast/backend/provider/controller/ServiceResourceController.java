package com.bookfast.backend.provider.controller;

import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.service.ResourceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/provider/resources")
public class ServiceResourceController {
    private final ResourceService service;

    public ServiceResourceController(ResourceService service) {
        this.service = service;
    }

    @GetMapping("/provider/{providerId}")
    public List<Resource> getResources(@PathVariable Long providerId) {
        return service.getResourcesByProvider(providerId);
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
        return service.createResource(resource); // Using createResource as update for now
    }

    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable Long id) {
        // Add delete method to ResourceService if needed
        // service.delete(id);
    }
}