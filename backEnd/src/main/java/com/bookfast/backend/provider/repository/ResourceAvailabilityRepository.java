package com.bookfast.backend.provider.repository;

import com.bookfast.backend.provider.model.ResourceAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceAvailabilityRepository extends JpaRepository<ResourceAvailability, Long> {
    List<ResourceAvailability> findByResourceId(Long resourceId);
}