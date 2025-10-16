package com.bookfast.backend.provider.controller;

import com.bookfast.backend.common.dto.ServiceResourceDto;
import com.bookfast.backend.provider.model.ServiceResource;
import com.bookfast.backend.provider.service.ServiceResourceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/provider/resources")
public class ServiceResourceController {
    private final ServiceResourceService service;

    public ServiceResourceController(ServiceResourceService service) {
        this.service = service;
    }

    @GetMapping("/provider/{providerId}")
    public List<ServiceResource> getResources(@PathVariable Long providerId) {
        return service.getResourcesByProvider(providerId);
    }

    @GetMapping("/{id}")
    public ServiceResource getResource(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }

@PostMapping
public ServiceResource createResource(@RequestBody ServiceResourceDto dto) {
    return service.createResourceFromDto(dto);
}

    @PutMapping("/{id}")
    public ServiceResource updateResource(@PathVariable Long id, @RequestBody ServiceResource resource) {
        resource.setId(id);
        return service.save(resource);
    }

    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable Long id) {
        service.delete(id);
    }
}