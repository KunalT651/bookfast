package com.bookfast.backend.provider.service;

import com.bookfast.backend.provider.model.UnavailableDate;
import com.bookfast.backend.provider.repository.UnavailableDateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ProviderService {
    private final UnavailableDateRepository unavailableDateRepository;
    private final String uploadDir = "uploads/profile-pictures/";

    public ProviderService(UnavailableDateRepository unavailableDateRepository) {
        this.unavailableDateRepository = unavailableDateRepository;
        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
        }
    }

    public String uploadProfilePicture(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = Paths.get(uploadDir + filename);
        Files.copy(file.getInputStream(), filePath);

        // Return URL (in production, this would be a full URL)
        return "/uploads/profile-pictures/" + filename;
    }

    public void markUnavailableDates(Long providerId, String startDate, String endDate, String reason) {
        UnavailableDate unavailableDate = new UnavailableDate();
        unavailableDate.setProviderId(providerId);
        unavailableDate.setStartDate(LocalDate.parse(startDate));
        unavailableDate.setEndDate(LocalDate.parse(endDate));
        unavailableDate.setReason(reason != null ? reason : "Vacation");
        
        unavailableDateRepository.save(unavailableDate);
    }

    public List<UnavailableDate> getUnavailableDates(Long providerId) {
        return unavailableDateRepository.findByProviderId(providerId);
    }

    public void removeUnavailableDate(Long id, Long providerId) {
        UnavailableDate unavailableDate = unavailableDateRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Unavailable date not found"));
        
        if (!unavailableDate.getProviderId().equals(providerId)) {
            throw new IllegalArgumentException("Access denied");
        }
        
        unavailableDateRepository.deleteById(id);
    }
}
