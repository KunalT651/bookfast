package com.bookfast.backend.common.repository;

import com.bookfast.backend.common.model.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {
    Optional<ProviderProfile> findByUserId(Long userId);
}