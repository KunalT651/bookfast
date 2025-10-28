package com.bookfast.backend.common.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName, lastName, email, password;
    private String organizationName; // For providers
    private String serviceCategory; // For providers
    private String imageUrl; // For profile picture
    private LocalDate createdDate; // For tracking user creation
    private Boolean isActive = true; // For tracking user status (active/inactive)
    
    // Google Calendar Integration
    private Boolean calendarConnected = false; // Whether user has connected Google Calendar
    private String calendarEmail; // User's Gmail address for calendar integration
    private String googleCalendarId; // Google Calendar ID (usually "primary")

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role; // <-- Use Role entity, not String

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Google Calendar Integration getters and setters
    public Boolean getCalendarConnected() {
        return calendarConnected;
    }

    public void setCalendarConnected(Boolean calendarConnected) {
        this.calendarConnected = calendarConnected;
    }

    public String getCalendarEmail() {
        return calendarEmail;
    }

    public void setCalendarEmail(String calendarEmail) {
        this.calendarEmail = calendarEmail;
    }

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }

    public void setGoogleCalendarId(String googleCalendarId) {
        this.googleCalendarId = googleCalendarId;
    }
}