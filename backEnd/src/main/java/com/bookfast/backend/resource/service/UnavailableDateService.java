package com.bookfast.backend.resource.service;

import com.bookfast.backend.resource.model.UnavailableDate;
import com.bookfast.backend.resource.repository.UnavailableDateRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class UnavailableDateService {
    private final UnavailableDateRepository repository;

    public UnavailableDateService(UnavailableDateRepository repository) {
        this.repository = repository;
    }

    public List<UnavailableDate> getUnavailableDates(Long providerId) {
        return repository.findByProviderId(providerId);
    }

    public UnavailableDate addUnavailableDate(Long providerId, LocalDate date) {
        UnavailableDate ud = new UnavailableDate();
        ud.setProviderId(providerId);
        ud.setDate(date);
        return repository.save(ud);
    }

    public void removeUnavailableDate(Long id) {
        repository.deleteById(id);
    }
}
