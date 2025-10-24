package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.model.Customer;
import com.bookfast.backend.resource.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerProfileController {
    private final CustomerService service;

    public CustomerProfileController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public Customer getProfile(@RequestParam Long id) {
        return service.getProfile(id);
    }

    @PutMapping
    public Customer updateProfile(@RequestParam Long id, @RequestBody Customer updated) {
        return service.updateProfile(id, updated);
    }
}
