package com.bookfast.backend.common.dto;

public class RegisterProviderRequest {
    public String firstName, lastName, email, password, confirmPassword, serviceCategory, organizationName;
public String role;
public String getRole() { return role; }
public void setRole(String role) { this.role = role; }
    // other getters/setters...
}