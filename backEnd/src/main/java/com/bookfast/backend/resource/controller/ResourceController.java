package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;

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
}