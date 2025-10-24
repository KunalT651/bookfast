package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.Customer;
import com.bookfast.backend.resource.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer getProfile(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Customer updateProfile(Long id, Customer updated) {
        Customer customer = repository.findById(id).orElse(null);
        if (customer != null) {
            customer.setName(updated.getName());
            customer.setEmail(updated.getEmail());
            customer.setPhone(updated.getPhone());
            customer.setZip(updated.getZip());
            return repository.save(customer);
        }
        return null;
    }
}
