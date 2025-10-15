package com.bookfast.backend.provider.repository;

import com.bookfast.backend.provider.model.ServiceResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceResourceRepository extends JpaRepository<ServiceResource, Long> {
    List<ServiceResource> findByProviderId(Long providerId);
}