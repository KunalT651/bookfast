package com.bookfast.backend.provider.repository;

import com.bookfast.backend.provider.model.ResourceAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceAvailabilityRepository extends JpaRepository<ResourceAvailability, Long> {
	// No ServiceResource references
}