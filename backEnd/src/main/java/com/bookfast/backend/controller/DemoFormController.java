package com.bookfast.backend.controller;

import com.bookfast.backend.model.DemoForm;
import com.bookfast.backend.repository.DemoFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "http://localhost:4200")
public class DemoFormController {

    @Autowired
    private DemoFormRepository repository;

    @GetMapping
    public List<DemoForm> getAll() {
        var formss = repository.findAll();
        return repository.findAll();
    }

    @PostMapping
    public DemoForm save(@RequestBody DemoForm form) {
        return repository.save(form);
    }
}