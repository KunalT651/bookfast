package com.bookfast.backend.common.auth.controller;

import com.bookfast.backend.common.auth.service.AuthService;
import com.bookfast.backend.common.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) {
        return authService.registerCustomer(req);
    }

    @PostMapping("/register-provider")
    public AuthResponse registerProvider(@RequestBody RegisterProviderRequest req) {
        return authService.registerProvider(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        return authService.login(req);
    }
}