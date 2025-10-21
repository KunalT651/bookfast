package com.bookfast.backend.common.auth.controller;

import com.bookfast.backend.common.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Standards-compliant cookie removal (Servlet 4.0+/Tomcat 9+)
        StringBuilder cookieHeader = new StringBuilder();
        cookieHeader.append("jwt=;")
            .append(" Max-Age=0;")
            .append(" Path=/;")
            .append(" HttpOnly;")
            .append(" SameSite=Lax");
        // Uncomment the next line for production
        // cookieHeader.append(" Secure;");
        response.setHeader("Set-Cookie", cookieHeader.toString());
        return ResponseEntity.ok(new AuthResponse("Logged out"));
    }
}