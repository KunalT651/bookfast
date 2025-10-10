package com.bookfast.backend.common.model;

import jakarta.persistence.*;

@Entity
public class ProviderProfile {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organizationName;
    private String serviceCategory;

    @OneToOne
    private User user;

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }
    public void setUser(User user) {
        this.user = user;
    }
    // getters/setters
}