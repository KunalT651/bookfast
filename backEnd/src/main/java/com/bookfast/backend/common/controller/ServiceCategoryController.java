package com.bookfast.backend.common.controller;

import com.bookfast.backend.common.model.ServiceCategory;
import com.bookfast.backend.common.repository.ServiceCategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceCategoryController {
    private final ServiceCategoryRepository repo;
    public ServiceCategoryController(ServiceCategoryRepository repo) { this.repo = repo; }

    @GetMapping("/categories")
    public List<ServiceCategory> getCategories() {
        return repo.findAll();
    }
}