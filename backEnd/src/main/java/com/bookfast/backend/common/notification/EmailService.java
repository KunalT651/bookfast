package com.bookfast.backend.common.notification;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${sendgrid.api-key:#{null}}")
    private String sendGridApiKey;
    
    @Value("${sendgrid.sender.email:noreply@bookfast.com}")
    private String senderEmail;
    
    @Value("${sendgrid.sender.name:BookFast}")
    private String senderName;

    public EmailService() {
        // Constructor for initialization
    }
    
    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("[EmailService] Initializing EmailService...");
        System.out.println("[EmailService] SendGrid API key configured: " + (sendGridApiKey != null && !sendGridApiKey.isEmpty()));
        System.out.println("[EmailService] Sender email: " + senderEmail);
        System.out.println("[EmailService] Sender name: " + senderName);
        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            System.err.println("[EmailService] WARNING: SendGrid API key is not configured!");
        } else {
            System.out.println("[EmailService] SendGrid API key length: " + sendGridApiKey.length());
            System.out.println("[EmailService] SendGrid API key starts with: " + (sendGridApiKey.length() > 3 ? sendGridApiKey.substring(0, 3) : "N/A"));
        }
    }

    public void sendEmail(String to, String subject, String content) throws Exception {
        // Check if API key is configured - fail silently if not (like old version)
        if (sendGridApiKey == null || sendGridApiKey.isEmpty() || sendGridApiKey.trim().isEmpty()) {
            System.err.println("[EmailService] WARNING: SendGrid API key is not configured. Email will not be sent.");
            return; // Silently return instead of throwing exception (like old version)
        }
        
        try {
            System.out.println("[EmailService] Sending email to: " + to);
            System.out.println("[EmailService] Subject: " + subject);
            
            // Use simpler approach like old version
            Email fromEmail = new Email(senderEmail != null && !senderEmail.isEmpty() ? senderEmail : "noreply@bookfast.com");
            Email toEmail = new Email(to);
            Content emailContent = new Content("text/plain", content);
            Mail mail = new Mail(fromEmail, subject, toEmail, emailContent);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            System.out.println("[EmailService] SendGrid response status: " + response.getStatusCode());
            
            // Log response but don't throw exception on non-200 (like old version)
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("[EmailService] Email sent successfully to: " + to);
            } else {
                System.err.println("[EmailService] SendGrid API returned status " + response.getStatusCode() + ": " + response.getBody());
                // Don't throw exception - just log the error (like old version)
            }
        } catch (Exception e) {
            System.err.println("[EmailService] Error sending email: " + e.getMessage());
            e.printStackTrace();
            // Don't rethrow - fail silently like old version
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws Exception {
        // Check if API key is configured - fail silently if not (like old version)
        if (sendGridApiKey == null || sendGridApiKey.isEmpty() || sendGridApiKey.trim().isEmpty()) {
            System.err.println("[EmailService] WARNING: SendGrid API key is not configured. Email will not be sent.");
            return; // Silently return instead of throwing exception (like old version)
        }
        
        try {
            System.out.println("[EmailService] Sending HTML email to: " + to);
            System.out.println("[EmailService] Subject: " + subject);
            
            // Use simpler approach like old version - just use sender email without name
            Email fromEmail = new Email(senderEmail != null && !senderEmail.isEmpty() ? senderEmail : "noreply@bookfast.com");
            Email toEmail = new Email(to);
            Content emailContent = new Content("text/html", htmlContent);
            Mail mail = new Mail(fromEmail, subject, toEmail, emailContent);
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            System.out.println("[EmailService] SendGrid response status: " + response.getStatusCode());
            
            // Log response but don't throw exception on non-200 (like old version)
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("[EmailService] HTML email sent successfully to: " + to);
            } else {
                System.err.println("[EmailService] SendGrid API returned status " + response.getStatusCode() + ": " + response.getBody());
                // Don't throw exception - just log the error (like old version)
            }
        } catch (Exception e) {
            System.err.println("[EmailService] Error sending email: " + e.getMessage());
            e.printStackTrace();
            // Don't rethrow - fail silently like old version
        }
    }
}