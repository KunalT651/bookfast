package com.bookfast.backend.resource.dto;

import java.time.LocalDateTime;

public class BookingDetailsDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerZip;
    private String status;
    private String paymentStatus;
    private Double finalAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String date;
    private String startTimeStr;
    private String endTimeStr;
    
    // Resource details
    private Long resourceId;
    private String resourceName;
    private String resourceDescription;
    private Double resourcePrice;
    private String resourceSpecialization;
    
    // Provider details
    private Long providerId;
    private String providerName;
    private String providerEmail;
    private String providerPhone;
    private String providerOrganization;
    private String providerServiceCategory;

    // Constructors
    public BookingDetailsDTO() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerZip() { return customerZip; }
    public void setCustomerZip(String customerZip) { this.customerZip = customerZip; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTimeStr() { return startTimeStr; }
    public void setStartTimeStr(String startTimeStr) { this.startTimeStr = startTimeStr; }

    public String getEndTimeStr() { return endTimeStr; }
    public void setEndTimeStr(String endTimeStr) { this.endTimeStr = endTimeStr; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getResourceDescription() { return resourceDescription; }
    public void setResourceDescription(String resourceDescription) { this.resourceDescription = resourceDescription; }

    public Double getResourcePrice() { return resourcePrice; }
    public void setResourcePrice(Double resourcePrice) { this.resourcePrice = resourcePrice; }

    public String getResourceSpecialization() { return resourceSpecialization; }
    public void setResourceSpecialization(String resourceSpecialization) { this.resourceSpecialization = resourceSpecialization; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProviderEmail() { return providerEmail; }
    public void setProviderEmail(String providerEmail) { this.providerEmail = providerEmail; }

    public String getProviderPhone() { return providerPhone; }
    public void setProviderPhone(String providerPhone) { this.providerPhone = providerPhone; }

    public String getProviderOrganization() { return providerOrganization; }
    public void setProviderOrganization(String providerOrganization) { this.providerOrganization = providerOrganization; }

    public String getProviderServiceCategory() { return providerServiceCategory; }
    public void setProviderServiceCategory(String providerServiceCategory) { this.providerServiceCategory = providerServiceCategory; }
}
