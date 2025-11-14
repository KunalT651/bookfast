package com.bookfast.backend.resource.controller;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.resource.service.AdminProviderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/providers")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminProviderController {
    private final AdminProviderService service;

    public AdminProviderController(AdminProviderService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAllProviders() {
        return service.getAllProviders();
    }

    @PutMapping("/{id}")
    public User updateProvider(@PathVariable Long id, @RequestBody User updated) {
        return service.updateProvider(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteProvider(@PathVariable Long id) {
        service.deleteProvider(id);
    }
}
