package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import com.bookfast.backend.common.repository.UserRepository;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.admin.repository.ServiceCategoryRepository;
import com.bookfast.backend.admin.model.ServiceCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class PublicProviderController {

    private final UserRepository userRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    public PublicProviderController(UserRepository userRepository,
                                    ServiceCategoryRepository serviceCategoryRepository) {
        this.userRepository = userRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    @GetMapping("/{providerId}/public")
    public Map<String, Object> getPublicProvider(@PathVariable Long providerId) {
        Map<String, Object> resp = new HashMap<>();
        Optional<User> opt = userRepository.findById(providerId);
        if (opt.isEmpty()) {
            resp.put("providerName", "");
            resp.put("serviceCategory", null);
            return resp;
        }
        User u = opt.get();
        String displayName = ( (u.getFirstName() != null && !u.getFirstName().isBlank()) ||
                               (u.getLastName() != null && !u.getLastName().isBlank()) )
                ? ((u.getFirstName() != null ? u.getFirstName() : "") + " " + (u.getLastName() != null ? u.getLastName() : "")).trim()
                : (u.getOrganizationName() != null && !u.getOrganizationName().isBlank() ? u.getOrganizationName() : u.getEmail());
        String catValue = u.getServiceCategory();
        String categoryName = null;
        if (catValue != null && !catValue.isBlank()) {
            boolean numeric = catValue.chars().allMatch(Character::isDigit);
            if (numeric) {
                try {
                    Long id = Long.valueOf(catValue);
                    Optional<ServiceCategory> sc = serviceCategoryRepository.findById(id);
                    categoryName = sc.map(ServiceCategory::getName).orElse(catValue);
                } catch (NumberFormatException ignored) {
                    categoryName = catValue;
                }
            } else {
                categoryName = catValue;
            }
        }
        resp.put("providerName", displayName);
        resp.put("serviceCategory", categoryName);
        return resp;
    }
}

