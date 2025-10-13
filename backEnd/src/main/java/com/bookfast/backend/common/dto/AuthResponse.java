package com.bookfast.backend.common.dto;

import com.bookfast.backend.common.model.User;

public class AuthResponse {
    public String token;
    public User user;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}