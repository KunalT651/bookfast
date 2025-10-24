package com.bookfast.backend.common.dto;

public class RegisterProviderRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private String serviceCategory;
    private String organizationName;
    private String role;

    // Default constructor
    public RegisterProviderRequest() {}

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getServiceCategory() { return serviceCategory; }
    public String getOrganizationName() { return organizationName; }
    public String getRole() { return role; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public void setRole(String role) { this.role = role; }
}