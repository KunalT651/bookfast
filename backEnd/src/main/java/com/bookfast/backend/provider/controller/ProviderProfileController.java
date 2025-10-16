package com.bookfast.backend.provider.controller;

import com.bookfast.backend.common.model.ProviderProfile;
import com.bookfast.backend.common.repository.ProviderProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provider")
public class ProviderProfileController {

    private final ProviderProfileRepository providerProfileRepository;

    @Autowired
    public ProviderProfileController(ProviderProfileRepository providerProfileRepository) {
        this.providerProfileRepository = providerProfileRepository;
    }

    @GetMapping("/profile/user/{userId}")
    public ResponseEntity<ProviderProfile> getByUserId(@PathVariable Long userId) {
        return providerProfileRepository.findByUserId(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}