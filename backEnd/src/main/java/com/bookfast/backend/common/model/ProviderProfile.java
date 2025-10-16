package com.bookfast.backend.common.model;

import jakarta.persistence.*;

@Entity
@Table(name = "provider_profile") // Optional, but recommended for clarity
public class ProviderProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organizationName;
    private String serviceCategory;

    @OneToOne
    @JoinColumn(name = "user_id") // Explicit join column
    private User user;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }

    public User getUser() {
        return user;
    }
    public void setUser(User user) { this.user = user; }
}