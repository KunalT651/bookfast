package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.repository.ResourceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cleanup")
public class PermanentCleanupController {
    private final ResourceRepository resourceRepository;

    public PermanentCleanupController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @PostMapping("/permanent-cleanup")
    public ResponseEntity<?> permanentCleanup() {
        try {
            long resourceCount = resourceRepository.count();
            resourceRepository.deleteAll();
            
            return ResponseEntity.ok(Map.of(
                "message", "Permanent cleanup completed",
                "deleted_resources", resourceCount,
                "note", "All resources have been deleted from the unified 'resource' table."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/disable-service-resources")
    public ResponseEntity<?> disableServiceResources() {
        return ResponseEntity.ok(Map.of(
            "message", "No action needed for service_resources table.",
            "note", "The ServiceResource entity and related components have been removed. All resources now use the unified 'resource' table."
        ));
    }

    @GetMapping("/check-cleanup")
    public ResponseEntity<?> checkCleanup() {
        try {
            long resourceCount = resourceRepository.count();
            
            return ResponseEntity.ok(Map.of(
                "resource_count", resourceCount,
                "cleanup_status", resourceCount == 0 ? "CLEAN" : "NEEDS_CLEANUP"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
