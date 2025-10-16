package com.bookfast.backend.common.dto;

import java.util.List;

public class ServiceResourceDto {
    public String name;
    public String address;
    public String description;
    public String email;
    public String imageUrl;
    public Integer experienceYears;
    public Double price;
    public String phone;
    public List<String> tags;
    public String specialization;
    public String status;
    public Long providerId;
    public List<ResourceAvailabilityDto> availabilities;

    // Getters and setters for all fields
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

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getProviderId() 
    { 
        return providerId;
     }

public List<ResourceAvailabilityDto> getAvailabilities() {
    return availabilities;
}

public void setAvailabilities(List<ResourceAvailabilityDto> availabilities) {
    this.availabilities = availabilities;
}

    public void setProviderId(Long providerId) { this.providerId = providerId; }

}