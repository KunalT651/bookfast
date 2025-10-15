package com.bookfast.backend.admin.service;

import com.bookfast.backend.admin.model.ServiceCategory;
import com.bookfast.backend.admin.repository.ServiceCategoryRepository;
import com.bookfast.backend.common.dto.ServiceCategoryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCategoryService {
    private final ServiceCategoryRepository repo;

    public ServiceCategoryService(ServiceCategoryRepository repo) {
        this.repo = repo;
    }

    public List<ServiceCategoryDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ServiceCategoryDto create(ServiceCategoryDto dto) {
        if (repo.existsByName(dto.name)) throw new RuntimeException("Category already exists");
        ServiceCategory cat = new ServiceCategory();
        cat.setName(dto.name);
        cat.setDescription(dto.description);
        repo.save(cat);
        return toDto(cat);
    }

    public ServiceCategoryDto update(Long id, ServiceCategoryDto dto) {
        ServiceCategory cat = repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        cat.setName(dto.name);
        cat.setDescription(dto.description);
        repo.save(cat);
        return toDto(cat);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private ServiceCategoryDto toDto(ServiceCategory cat) {
        ServiceCategoryDto dto = new ServiceCategoryDto();
        dto.id = cat.getId();
        dto.name = cat.getName();
        dto.description = cat.getDescription();
        return dto;
    }
}