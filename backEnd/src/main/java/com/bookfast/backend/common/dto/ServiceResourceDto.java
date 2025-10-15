package com.bookfast.backend.common.dto;

import java.util.List;

public class ServiceResourceDto {
    public Long id;
    public String name;
    public String contactNumber;
    public String specialization;
    public String description;
    public String status;
    public Long providerId;
    public List<ResourceAvailabilityDto> availabilities;
}