package com.bookfast.backend.admin.controller;

import com.bookfast.backend.admin.service.AdminProviderService;
import com.bookfast.backend.common.dto.ErrorResponse;
import com.bookfast.backend.common.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/providers")
public class AdminProviderController {
    private final AdminProviderService adminProviderService;

    public AdminProviderController(AdminProviderService adminProviderService) {
        this.adminProviderService = adminProviderService;
    }

    @GetMapping
    public ResponseEntity<?> getAllProviders() {
        try {
            List<User> providers = adminProviderService.getAllProviders();
            return ResponseEntity.ok(providers);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch providers: " + ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable Long id) {
        try {
            User provider = adminProviderService.getProviderById(id);
            if (provider == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(provider);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch provider: " + ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createProvider(@RequestBody User provider) {
        try {
            User createdProvider = adminProviderService.createProvider(provider);
            return ResponseEntity.ok(createdProvider);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create provider: " + ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(@PathVariable Long id, @RequestBody User provider) {
        try {
            User updatedProvider = adminProviderService.updateProvider(id, provider);
            if (updatedProvider == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedProvider);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update provider: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(@PathVariable Long id) {
        try {
            boolean deleted = adminProviderService.deleteProvider(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete provider: " + ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateProviderStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        try {
            boolean updated = adminProviderService.updateProviderStatus(id, request.isActive);
            if (!updated) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update provider status: " + ex.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProvidersByCategory(@PathVariable String category) {
        try {
            List<User> providers = adminProviderService.getProvidersByCategory(category);
            return ResponseEntity.ok(providers);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch providers by category: " + ex.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProviders(@RequestParam String q) {
        try {
            List<User> providers = adminProviderService.searchProviders(q);
            return ResponseEntity.ok(providers);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to search providers: " + ex.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getProviderStats() {
        try {
            Object stats = adminProviderService.getProviderStats();
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch provider stats: " + ex.getMessage()));
        }
    }

    public static class UpdateStatusRequest {
        public boolean isActive;
    }
}
