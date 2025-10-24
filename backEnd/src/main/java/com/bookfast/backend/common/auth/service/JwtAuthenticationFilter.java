package com.bookfast.backend.common.auth.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        // 1. Try Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("[JWT] Found token in Authorization header: " + token);
        }
        // 2. Fallback to cookie
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    System.out.println("[JWT] Found token in cookie: " + token);
                    break;
                }
            }
        }
        if (token == null) {
            System.out.println("[JWT] No JWT found in header or cookie. Treating request as unauthenticated.");
        } else if (!jwtService.validateToken(token)) {
            System.out.println("[JWT] Invalid or expired JWT token. Treating request as unauthenticated.");
        } else {
            try {
                Claims claims = jwtService.parseClaims(token);
                String username = claims.getSubject();
                List<?> rawAuthorities = claims.get("authorities", List.class);
                List<String> authorities = rawAuthorities != null
                        ? rawAuthorities.stream().map(Object::toString).collect(Collectors.toList())
                        : null;
                System.out.println("[JWT] Parsed claims: username=" + username + ", authorities=" + authorities);
                if (username != null && authorities != null) {
                    var grantedAuthorities = authorities.stream()
                            .map(auth -> auth.startsWith("ROLE_") ? auth : "ROLE_" + auth)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, grantedAuthorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("[JWT] Authenticated user: " + username + ", roles: " + authorities);
                } else {
                    System.out.println(
                            "[JWT] JWT claims missing username or authorities. Treating request as unauthenticated.");
                }
            } catch (Exception e) {
                System.out.println("[JWT] Exception parsing JWT: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}