package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.AvailabilitySlot;
import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.AvailabilitySlotRepository;
import com.bookfast.backend.resource.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final ResourceRepository resourceRepository;

    public AvailabilitySlotService(AvailabilitySlotRepository availabilitySlotRepository, ResourceRepository resourceRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.resourceRepository = resourceRepository;
    }

    public List<AvailabilitySlot> getAvailabilitySlotsForResource(Long resourceId) {
        return availabilitySlotRepository.findByResourceId(resourceId);
    }

    public List<AvailabilitySlot> getAvailabilitySlotsForResourceAndDate(Long resourceId, LocalDate date) {
        return availabilitySlotRepository.findByResourceIdAndDate(resourceId, date);
    }

    public AvailabilitySlot createAvailabilitySlot(Long resourceId, LocalDate date, LocalTime startTime, LocalTime endTime, String status) {
        Resource resource = resourceRepository.findById(resourceId)
                                .orElseThrow(() -> new RuntimeException("Resource not found"));

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setResource(resource);
        slot.setDate(date);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setStatus(status);
        return availabilitySlotRepository.save(slot);
    }

    public Optional<AvailabilitySlot> getAvailabilitySlotById(Long id) {
        return availabilitySlotRepository.findById(id);
    }

    public AvailabilitySlot updateAvailabilitySlot(Long id, LocalDate date, LocalTime startTime, LocalTime endTime, String status) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Availability slot not found"));
        slot.setDate(date);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setStatus(status);
        return availabilitySlotRepository.save(slot);
    }

    public void deleteAvailabilitySlot(Long id) {
        availabilitySlotRepository.deleteById(id);
    }
}
