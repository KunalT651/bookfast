package com.bookfast.backend.provider.controller;

import com.bookfast.backend.provider.model.ResourceAvailability;
import com.bookfast.backend.provider.service.ResourceAvailabilityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider/availabilities")
public class ResourceAvailabilityController {
    private final ResourceAvailabilityService service;

    public ResourceAvailabilityController(ResourceAvailabilityService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public ResourceAvailability getAvailability(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }

    @PostMapping
    public ResourceAvailability createAvailability(@RequestBody ResourceAvailability availability) {
        return service.save(availability);
    }

    @PutMapping("/{id}")
    public ResourceAvailability updateAvailability(@PathVariable Long id, @RequestBody ResourceAvailability availability) {
        availability.setId(id);
        return service.save(availability);
    }

    @DeleteMapping("/{id}")
    public void deleteAvailability(@PathVariable Long id) {
        service.delete(id);
    }
}