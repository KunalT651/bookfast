package com.bookfast.backend.common.dto;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
    private String confirmPassword;

    // Default constructor
    public RegisterRequest() {}

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}