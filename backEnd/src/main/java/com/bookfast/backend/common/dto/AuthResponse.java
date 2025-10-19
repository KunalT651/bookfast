package com.bookfast.backend.common.dto;

import com.bookfast.backend.common.model.User;

public class AuthResponse {
    public String token;
    public User user;
    public String message;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}