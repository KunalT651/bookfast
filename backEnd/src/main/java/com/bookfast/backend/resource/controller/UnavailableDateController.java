package com.bookfast.backend.resource.controller;

import com.bookfast.backend.resource.model.UnavailableDate;
import com.bookfast.backend.resource.service.UnavailableDateService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/provider/unavailable-dates")
@CrossOrigin(origins = "http://localhost:4200")
public class UnavailableDateController {
    private final UnavailableDateService service;

    public UnavailableDateController(UnavailableDateService service) {
        this.service = service;
    }

    @GetMapping("/{providerId}")
    public List<UnavailableDate> getUnavailableDates(@PathVariable Long providerId) {
        return service.getUnavailableDates(providerId);
    }

    @PostMapping
    public UnavailableDate addUnavailableDate(@RequestParam Long providerId, @RequestParam String date) {
        return service.addUnavailableDate(providerId, LocalDate.parse(date));
    }

    @DeleteMapping("/{id}")
    public void removeUnavailableDate(@PathVariable Long id) {
        service.removeUnavailableDate(id);
    }
}
