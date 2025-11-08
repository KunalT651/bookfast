package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.AvailabilitySlot;
import com.bookfast.backend.resource.model.Resource;
import com.bookfast.backend.resource.repository.AvailabilitySlotRepository;
import com.bookfast.backend.resource.repository.ResourceRepository;
import com.bookfast.backend.provider.model.UnavailableDate;
import com.bookfast.backend.provider.repository.UnavailableDateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final ResourceRepository resourceRepository;
    private final UnavailableDateRepository unavailableDateRepository;

    public AvailabilitySlotService(AvailabilitySlotRepository availabilitySlotRepository, 
                                  ResourceRepository resourceRepository,
                                  UnavailableDateRepository unavailableDateRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.resourceRepository = resourceRepository;
        this.unavailableDateRepository = unavailableDateRepository;
    }

    public List<AvailabilitySlot> getAvailabilitySlotsForResource(Long resourceId) {
        List<AvailabilitySlot> slots = availabilitySlotRepository.findByResourceId(resourceId);
        
        // Get the provider ID from the resource
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (resourceOpt.isPresent()) {
            Long providerId = resourceOpt.get().getProviderId();
            
            // Get all unavailable dates for this provider
            List<UnavailableDate> unavailableDates = unavailableDateRepository.findByProviderId(providerId);
            
            // Mark slots as unavailable if they fall within unavailable date ranges
            for (AvailabilitySlot slot : slots) {
                if (slot.getDate() != null && isDateUnavailable(slot.getDate(), unavailableDates)) {
                    slot.setStatus("unavailable");
                }
            }
        }
        
        return slots;
    }
    
    private boolean isDateUnavailable(LocalDate date, List<UnavailableDate> unavailableDates) {
        for (UnavailableDate unavailableDate : unavailableDates) {
            if (!date.isBefore(unavailableDate.getStartDate()) && !date.isAfter(unavailableDate.getEndDate())) {
                return true;
            }
        }
        return false;
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
