package com.bookfast.backend.admin.controller;

import com.bookfast.backend.admin.service.ServiceCategoryService;
import com.bookfast.backend.common.dto.ServiceCategoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class ServiceCategoryController {
    private final ServiceCategoryService service;

    public ServiceCategoryController(ServiceCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceCategoryDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<ServiceCategoryDto> create(@RequestBody ServiceCategoryDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceCategoryDto> update(@PathVariable Long id, @RequestBody ServiceCategoryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}