package com.bookfast.backend.common.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    @Value("${twilio.account.sid:}")
    private String accountSid;
    
    @Value("${twilio.auth.token:}")
    private String authToken;
    
    @Value("${twilio.phone.number:}")
    private String fromPhoneNumber;
    
    private boolean twilioInitialized = false;
    
    /**
     * Initialize Twilio with credentials
     */
    private void initTwilio() {
        if (!twilioInitialized && accountSid != null && !accountSid.isEmpty() 
            && authToken != null && !authToken.isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                twilioInitialized = true;
                System.out.println("[SmsService] Twilio initialized successfully");
            } catch (Exception e) {
                System.err.println("[SmsService] Failed to initialize Twilio: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send SMS message
     * @param toPhoneNumber Recipient phone number in E.164 format (e.g., +12345678900)
     * @param messageBody SMS message content
     * @return true if SMS sent successfully, false otherwise
     */
    public boolean sendSms(String toPhoneNumber, String messageBody) {
        // Check if Twilio is configured
        if (accountSid == null || accountSid.isEmpty() || 
            authToken == null || authToken.isEmpty() ||
            fromPhoneNumber == null || fromPhoneNumber.isEmpty()) {
            System.out.println("[SmsService] Twilio not configured. Skipping SMS.");
            return false;
        }
        
        // Initialize Twilio if not already done
        if (!twilioInitialized) {
            initTwilio();
        }
        
        // Skip if phone number is not provided
        if (toPhoneNumber == null || toPhoneNumber.trim().isEmpty()) {
            System.out.println("[SmsService] No phone number provided. Skipping SMS.");
            return false;
        }
        
        try {
            // Ensure phone number is in E.164 format
            String formattedNumber = formatPhoneNumber(toPhoneNumber);
            
            Message message = Message.creator(
                new PhoneNumber(formattedNumber),  // To number
                new PhoneNumber(fromPhoneNumber),  // From number (your Twilio number)
                messageBody                         // SMS body
            ).create();
            
            System.out.println("[SmsService] SMS sent successfully to " + toPhoneNumber + " (SID: " + message.getSid() + ")");
            return true;
            
        } catch (Exception e) {
            System.err.println("[SmsService] Failed to send SMS to " + toPhoneNumber + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Format phone number to E.164 format
     * If it starts with +1, keep it. If it's 10 digits, add +1
     */
    private String formatPhoneNumber(String phoneNumber) {
        // Remove all non-digit characters except +
        String cleaned = phoneNumber.replaceAll("[^+\\d]", "");
        
        // If it already starts with +, return as is
        if (cleaned.startsWith("+")) {
            return cleaned;
        }
        
        // If it's 10 digits, assume it's US/Canada and add +1
        if (cleaned.length() == 10) {
            return "+1" + cleaned;
        }
        
        // If it's 11 digits starting with 1, add +
        if (cleaned.length() == 11 && cleaned.startsWith("1")) {
            return "+" + cleaned;
        }
        
        // Otherwise return as is and hope for the best
        return cleaned;
    }
    
    /**
     * Check if SMS service is configured and available
     */
    public boolean isConfigured() {
        return accountSid != null && !accountSid.isEmpty() && 
               authToken != null && !authToken.isEmpty() &&
               fromPhoneNumber != null && !fromPhoneNumber.isEmpty();
    }
}

