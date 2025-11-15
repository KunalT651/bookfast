package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/provider/profile/upload-picture")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class ProviderProfilePictureController {

    private final UserRepository userRepository;
    
    @org.springframework.beans.factory.annotation.Value("${imgur.client.id:546b2e1e7b1b1e7}")
    private String imgurClientId;

    public ProviderProfilePictureController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("[ProviderProfilePictureController] Uploading profile picture");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // This is the email

            User currentUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            if (file == null || file.isEmpty()) {
                System.out.println("[ProviderProfilePictureController] No file provided");
                return ResponseEntity.badRequest().body(Map.of("error", "No file provided"));
            }

            // Validate file type - only PNG and JPG allowed
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() 
                : "";
            
            boolean isValidType = (contentType != null && (contentType.equals("image/png") || contentType.equals("image/jpeg") || contentType.equals("image/jpg")))
                || fileExtension.equals("png") || fileExtension.equals("jpg") || fileExtension.equals("jpeg");
            
            if (!isValidType) {
                System.out.println("[ProviderProfilePictureController] Invalid file type: " + contentType + " / " + fileExtension);
                return ResponseEntity.badRequest().body(Map.of("error", "Only PNG and JPG files are allowed."));
            }

            // Validate file size (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                System.out.println("[ProviderProfilePictureController] File too large: " + file.getSize() + " bytes");
                return ResponseEntity.badRequest().body(Map.of("error", "File size must be less than 5MB."));
            }

            System.out.println("[ProviderProfilePictureController] File received: " + file.getOriginalFilename() + ", size: " + file.getSize() + ", type: " + contentType);
            
            // Store the previous imageUrl in case upload fails (keep existing photo)
            String previousImageUrl = currentUser.getImageUrl();
            System.out.println("[ProviderProfilePictureController] Previous imageUrl (will keep if upload fails): " + previousImageUrl);
            
            // Upload to Imgur (deployment-ready cloud storage)
            // In production, consider using AWS S3, Cloudinary, or similar service
            // Imgur returns full URL (https://i.imgur.com/...), which is deployment-ready
            String imageUrl;
            try {
                System.out.println("[ProviderProfilePictureController] Attempting to upload to Imgur...");
                imageUrl = uploadToImgur(file);
                System.out.println("[ProviderProfilePictureController] Successfully uploaded to Imgur: " + imageUrl);
            } catch (Exception imgurError) {
                System.err.println("[ProviderProfilePictureController] Imgur upload failed: " + imgurError.getMessage());
                imgurError.printStackTrace();
                // For deployment-ready code, we don't fall back to local storage
                // Keep the previous imageUrl (if no photo was there before, it stays null)
                // Return error but don't change the existing photo
                String errorMessage = "Failed to upload profile picture to cloud storage.";
                if (imgurError.getMessage() != null && imgurError.getMessage().contains("429")) {
                    errorMessage = "Upload service is temporarily unavailable due to rate limits. Please try again in a few minutes.";
                } else if (imgurError.getMessage() != null && imgurError.getMessage().contains("Connection")) {
                    errorMessage = "Unable to connect to upload service. Please check your internet connection and try again.";
                }
                return ResponseEntity.status(500).body(Map.of(
                    "error", errorMessage,
                    "previousUrl", previousImageUrl != null ? previousImageUrl : ""
                ));
            }

            // Store the full URL in database (deployment-ready)
            // The imageUrl from Imgur is already a full URL (https://i.imgur.com/...)
            currentUser.setImageUrl(imageUrl);
            userRepository.save(currentUser);
            System.out.println("[ProviderProfilePictureController] Profile picture URL saved to database: " + imageUrl);

            System.out.println("[ProviderProfilePictureController] Profile picture uploaded successfully: " + imageUrl);
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("[ProviderProfilePictureController] Error uploading profile picture: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload profile picture: " + e.getMessage()));
        }
    }

    private String uploadToImgur(MultipartFile image) throws Exception {
        // Use Imgur API (configurable client ID via IMGUR_CLIENT_ID environment variable)
        if (imgurClientId == null || imgurClientId.isEmpty()) {
            throw new Exception("Imgur Client ID not configured. Set IMGUR_CLIENT_ID environment variable.");
        }
        java.net.URI uri = java.net.URI.create("https://api.imgur.com/3/image");
        java.net.URL url = java.net.URL.of(uri, null);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Client-ID " + imgurClientId);
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setDoOutput(true);
        
        // Write image bytes
        java.io.OutputStream os = conn.getOutputStream();
        os.write(image.getBytes());
        os.close();
        
        // Check response code
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            // Read error response
            java.io.InputStream errorStream = conn.getErrorStream();
            String errorResponse = "";
            if (errorStream != null) {
                java.util.Scanner s = new java.util.Scanner(errorStream).useDelimiter("\\A");
                errorResponse = s.hasNext() ? s.next() : "";
                s.close();
                errorStream.close();
            }
            throw new Exception("Imgur API returned HTTP " + responseCode + ": " + errorResponse);
        }
        
        // Read success response
        java.io.InputStream is = conn.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        s.close();
        is.close();
        
        // Parse Imgur response for link
        int linkIdx = response.indexOf("link");
        if (linkIdx == -1) {
            throw new Exception("Invalid Imgur response: link not found");
        }
        int urlStart = response.indexOf('"', linkIdx + 6) + 1;
        int urlEnd = response.indexOf('"', urlStart);
        if (urlStart <= 0 || urlEnd <= urlStart) {
            throw new Exception("Invalid Imgur response: could not parse image URL");
        }
        return response.substring(urlStart, urlEnd);
    }
}
