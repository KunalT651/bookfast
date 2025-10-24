package com.bookfast.backend.resource.repository;

import com.bookfast.backend.resource.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByResourceIdAndDate(Long resourceId, LocalDate date);
    List<AvailabilitySlot> findByResourceId(Long resourceId);
}
