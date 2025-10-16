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
    private String address;
    private String description;
    private String email;
    private String imageUrl;
    private Integer experienceYears;
    private Double price;
    private String phone;
    private String specialization;
    private String status;
    @ElementCollection
    private List<String> tags;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderProfile provider;

@OneToMany(mappedBy = "serviceResource", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ResourceAvailability> availabilities;
    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public ProviderProfile getProvider() { return provider; }
    public void setProvider(ProviderProfile provider) { this.provider = provider; }

    public List<ResourceAvailability> getAvailabilities() { return availabilities; }
    public void setAvailabilities(List<ResourceAvailability> availabilities) { this.availabilities = availabilities; }
}