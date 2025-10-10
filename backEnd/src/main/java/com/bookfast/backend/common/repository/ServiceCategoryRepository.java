package com.bookfast.backend.common.repository;

import com.bookfast.backend.common.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    // No extra code needed for findAll()
}