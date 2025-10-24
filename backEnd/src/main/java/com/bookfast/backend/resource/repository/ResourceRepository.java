package com.bookfast.backend.resource.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookfast.backend.resource.model.Resource;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findBySpecializationAndStatus(String specialization, String status);

    List<Resource> findBySpecialization(String specialization);

    List<Resource> findByProviderId(Long providerId);

    List<Resource> findByStatus(String status);
}