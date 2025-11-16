package com.bookfast.backend.resource.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "resource")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id")
    @JsonProperty("providerId")
    private Long providerId;

    private String name;
    private String description;
    private String specialization;
    private String status;
    private Double price;

    @Column(name = "experience_years")
    @JsonProperty("experienceYears")
    private Integer experienceYears;

    @Column(name = "contact_number")
    @JsonProperty("phone")
    private String phone;

    private String email;

    @Column(name = "image_url")
    @JsonProperty("imageUrl")
    private String imageUrl;

    @ElementCollection
    private List<String> tags;

    private Double rating;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<AvailabilitySlot> availabilitySlots;

    // Transient field to hold provider's service category (not stored in database)
    @Transient
    @JsonProperty("serviceCategory")
    private String serviceCategory;

    // Transient field to indicate if resource has available slots (not stored in database)
    @Transient
    @JsonProperty("hasAvailableSlots")
    private Boolean hasAvailableSlots;

    // Transient field to expose provider's display name (not stored in database)
    @Transient
    @JsonProperty("providerName")
    private String providerName;
    public List<AvailabilitySlot> getAvailabilitySlots() { return availabilitySlots; }
    public void setAvailabilitySlots(List<AvailabilitySlot> availabilitySlots) { this.availabilitySlots = availabilitySlots; }

    public String getServiceCategory() { return serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }

    public Boolean getHasAvailableSlots() { return hasAvailableSlots; }
    public void setHasAvailableSlots(Boolean hasAvailableSlots) { this.hasAvailableSlots = hasAvailableSlots; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}