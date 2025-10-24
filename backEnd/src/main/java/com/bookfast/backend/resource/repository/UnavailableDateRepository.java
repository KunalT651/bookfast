package com.bookfast.backend.resource.repository;

import com.bookfast.backend.resource.model.UnavailableDate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnavailableDateRepository extends JpaRepository<UnavailableDate, Long> {
    List<UnavailableDate> findByProviderId(Long providerId);
}
