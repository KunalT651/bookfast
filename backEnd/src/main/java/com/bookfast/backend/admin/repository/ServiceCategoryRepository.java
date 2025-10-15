package com.bookfast.backend.admin.repository;

import com.bookfast.backend.admin.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    boolean existsByName(String name);
}