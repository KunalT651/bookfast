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
@CrossOrigin(origins = "http://localhost:4200")
public class ProviderProfilePictureController {

    private final UserRepository userRepository;

    public ProviderProfilePictureController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // This is the email

        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found."));

        String imageUrl = uploadToImgur(file); // Assuming uploadToImgur handles the file upload

        currentUser.setImageUrl(imageUrl);
        userRepository.save(currentUser);

        Map<String, String> result = new HashMap<>();
        result.put("url", imageUrl);
        return ResponseEntity.ok(result);
    }

    private String uploadToImgur(MultipartFile image) throws Exception {
        // Use Imgur API (demo, no error handling)
        java.net.URI uri = java.net.URI.create("https://api.imgur.com/3/image");
        java.net.URL url = java.net.URL.of(uri, null);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Client-ID 546b2e1e7b1b1e7"); // Demo client ID
        conn.setDoOutput(true);
        java.io.OutputStream os = conn.getOutputStream();
        os.write(image.getBytes());
        os.close();
        java.io.InputStream is = conn.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        s.close();
        is.close();
        // Parse Imgur response for link
        int linkIdx = response.indexOf("link");
        int urlStart = response.indexOf('"', linkIdx + 6) + 1;
        int urlEnd = response.indexOf('"', urlStart);
        return response.substring(urlStart, urlEnd);
    }
}
