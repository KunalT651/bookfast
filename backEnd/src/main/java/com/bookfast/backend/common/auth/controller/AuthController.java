package com.bookfast.backend.common.auth.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.auth.service.JwtService;
import com.bookfast.backend.common.dto.*;
import com.bookfast.backend.common.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            return ResponseEntity.ok(authService.registerCustomer(req));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/register-provider")
    public ResponseEntity<?> registerProvider(@RequestBody RegisterProviderRequest req) {
        try {
            return ResponseEntity.ok(authService.registerProvider(req));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.login(req);
            // authResponse should contain JWT, user info, etc.
            String jwt = authResponse.getToken();
            // For cross-domain deployment (Vercel frontend + Render backend)
            // We need SameSite=None and Secure=true
            StringBuilder cookieHeader = new StringBuilder();
            cookieHeader.append("jwt=").append(jwt)
                .append("; Max-Age=").append(7 * 24 * 60 * 60)
                .append("; Path=/")
                .append("; HttpOnly")
                .append("; SameSite=None")
                .append("; Secure"); // Required for SameSite=None
            response.setHeader("Set-Cookie", cookieHeader.toString());
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@CookieValue(name = "jwt", required = false) String jwt) {
        if (jwt == null) {
            return ResponseEntity.status(401).body(new ErrorResponse("Unauthorized"));
        }
        try {
            String email = jwtService.extractUsername(jwt);
            User user = authService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body(new ErrorResponse("User not found"));
            }
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(new ErrorResponse("Invalid token"));
        }
    }
}