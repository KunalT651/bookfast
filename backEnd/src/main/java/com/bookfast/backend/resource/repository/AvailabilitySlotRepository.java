package com.bookfast.backend.resource.repository;

import com.bookfast.backend.resource.model.AvailabilitySlot;
import com.bookfast.backend.resource.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByResource(Resource resource);
    List<AvailabilitySlot> findByResourceId(Long resourceId);
    List<AvailabilitySlot> findByResourceIdAndDayOfWeek(Long resourceId, java.time.DayOfWeek dayOfWeek);
}
