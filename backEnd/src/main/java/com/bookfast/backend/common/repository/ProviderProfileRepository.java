package com.bookfast.backend.common.repository;

import com.bookfast.backend.common.model.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {
    // No extra code needed for save()
}