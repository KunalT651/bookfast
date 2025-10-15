package com.bookfast.backend.provider.model;

import com.bookfast.backend.common.model.ProviderProfile;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class ServiceResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contactNumber;
    private String specialization;
    private String description;
    private String status; // active/inactive

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderProfile provider;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceAvailability> availabilities;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ProviderProfile getProvider() { return provider; }
    public void setProvider(ProviderProfile provider) { this.provider = provider; }

    public List<ResourceAvailability> getAvailabilities() { return availabilities; }
    public void setAvailabilities(List<ResourceAvailability> availabilities) { this.availabilities = availabilities; }
}