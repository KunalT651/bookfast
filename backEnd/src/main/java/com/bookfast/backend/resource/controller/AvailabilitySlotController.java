package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.model.AvailabilitySlot;
import com.bookfast.backend.resource.service.AvailabilitySlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources/{resourceId}/availability")
public class AvailabilitySlotController {
    private final AvailabilitySlotService availabilitySlotService;

    public AvailabilitySlotController(AvailabilitySlotService availabilitySlotService) {
        this.availabilitySlotService = availabilitySlotService;
    }

    @GetMapping
    public ResponseEntity<List<AvailabilitySlot>> getAvailabilitySlots(@PathVariable Long resourceId) {
        List<AvailabilitySlot> slots = availabilitySlotService.getAvailabilitySlotsForResource(resourceId);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<AvailabilitySlot> getAvailabilitySlotById(@PathVariable Long slotId) {
        return availabilitySlotService.getAvailabilitySlotById(slotId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AvailabilitySlot> createAvailabilitySlot(
            @PathVariable Long resourceId,
            @RequestBody Map<String, String> payload) {
        try {
            LocalDate date = LocalDate.parse(payload.get("date"));
            LocalTime startTime = LocalTime.parse(payload.get("startTime"));
            LocalTime endTime = LocalTime.parse(payload.get("endTime"));
            String status = payload.getOrDefault("status", "available");

            AvailabilitySlot newSlot = availabilitySlotService.createAvailabilitySlot(resourceId, date, startTime, endTime, status);
            return ResponseEntity.ok(newSlot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Handle error properly
        }
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<AvailabilitySlot> updateAvailabilitySlot(
            @PathVariable Long slotId,
            @RequestBody Map<String, String> payload) {
        try {
            LocalDate date = LocalDate.parse(payload.get("date"));
            LocalTime startTime = LocalTime.parse(payload.get("startTime"));
            LocalTime endTime = LocalTime.parse(payload.get("endTime"));
            String status = payload.getOrDefault("status", "available");

            AvailabilitySlot updatedSlot = availabilitySlotService.updateAvailabilitySlot(slotId, date, startTime, endTime, status);
            return ResponseEntity.ok(updatedSlot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Handle error properly
        }
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> deleteAvailabilitySlot(@PathVariable Long slotId) {
        availabilitySlotService.deleteAvailabilitySlot(slotId);
        return ResponseEntity.ok().build();
    }
}
