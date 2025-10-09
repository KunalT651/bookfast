package com.bookfast.backend.repository;

import com.bookfast.backend.model.DemoForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoFormRepository extends JpaRepository<DemoForm, Long> {
}