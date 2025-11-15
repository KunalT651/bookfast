package com.bookfast.backend.provider.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProviderService {
    private final String uploadDir = "uploads/profile-pictures/";

    public ProviderService() {
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
}
