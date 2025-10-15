package com.bookfast.backend.common.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ResourceAvailabilityDto {
    public Long id;
    public LocalDate date;
    public LocalTime startTime;
    public LocalTime endTime;
    public String status;
    public Long resourceId;
}