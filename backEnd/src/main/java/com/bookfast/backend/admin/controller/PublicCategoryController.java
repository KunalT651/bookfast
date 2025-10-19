package com.bookfast.backend.admin.controller;

import com.bookfast.backend.admin.service.ServiceCategoryService;
import com.bookfast.backend.common.dto.ServiceCategoryDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class PublicCategoryController {
    private final ServiceCategoryService service;

    public PublicCategoryController(ServiceCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceCategoryDto> getAll() {
        return service.getAll();
    }
}
