package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.model.AvailabilitySlot;
import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.AvailabilitySlotRepository;
import com.bookfast.backend.resource.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/availability-slots")
public class AvailabilitySlotController {
    @Autowired
    private AvailabilitySlotRepository slotRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<AvailabilitySlot>> getSlotsForResource(@PathVariable Long resourceId) {
        List<AvailabilitySlot> slots = slotRepository.findByResourceId(resourceId);
        return ResponseEntity.ok(slots);
    }

    @PostMapping("/resource/{resourceId}")
    public ResponseEntity<?> addSlotToResource(@PathVariable Long resourceId, @RequestBody AvailabilitySlot slot) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (resourceOpt.isEmpty()) return ResponseEntity.notFound().build();
        slot.setResource(resourceOpt.get());
        AvailabilitySlot saved = slotRepository.save(slot);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long slotId) {
        slotRepository.deleteById(slotId);
        return ResponseEntity.ok().build();
    }
}
