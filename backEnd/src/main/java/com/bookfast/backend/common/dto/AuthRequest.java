package com.bookfast.backend.common.dto;

public class AuthRequest {
    private String email;
    private String password;

    // Default constructor
    public AuthRequest() {}

    // Getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}