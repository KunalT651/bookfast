package com.bookfast.backend.common.notification;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${sendgrid.api-key:}")
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
        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            throw new IllegalStateException("SendGrid API key is not configured. Please set SENDGRID_API_KEY environment variable.");
        }
        
        System.out.println("[EmailService] Sending email to: " + to);
        System.out.println("[EmailService] Subject: " + subject);
        System.out.println("[EmailService] From: " + senderEmail);
        System.out.println("[EmailService] SendGrid API key configured: " + (sendGridApiKey != null && !sendGridApiKey.isEmpty()));
        
        Email fromEmail = new Email(senderEmail);
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
        System.out.println("[EmailService] SendGrid response body: " + response.getBody());
        System.out.println("[EmailService] SendGrid response headers: " + response.getHeaders());
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            System.out.println("[EmailService] Email sent successfully to: " + to);
        } else {
            throw new Exception("SendGrid API returned status " + response.getStatusCode() + ": " + response.getBody());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws Exception {
        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            throw new IllegalStateException("SendGrid API key is not configured. Please set SENDGRID_API_KEY environment variable.");
        }
        
        System.out.println("[EmailService] Sending HTML email to: " + to);
        System.out.println("[EmailService] Subject: " + subject);
        System.out.println("[EmailService] From: " + senderEmail + " (" + senderName + ")");
        System.out.println("[EmailService] SendGrid API key configured: " + (sendGridApiKey != null && !sendGridApiKey.isEmpty()));
        
        // Create from email with name for better deliverability
        Email fromEmail = new Email(senderEmail, senderName);
        Email toEmail = new Email(to);
        Content emailContent = new Content("text/html", htmlContent);
        
        // Use simpler Mail constructor for better compatibility
        Mail mail = new Mail(fromEmail, subject, toEmail, emailContent);
        
        // Add reply-to (same as from for better deliverability)
        mail.setReplyTo(fromEmail);
        
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        
        Response response = sg.api(request);
        System.out.println("[EmailService] SendGrid response status: " + response.getStatusCode());
        System.out.println("[EmailService] SendGrid response body: " + response.getBody());
        System.out.println("[EmailService] SendGrid response headers: " + response.getHeaders());
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            System.out.println("[EmailService] HTML email sent successfully to: " + to);
        } else {
            throw new Exception("SendGrid API returned status " + response.getStatusCode() + ": " + response.getBody());
        }
    }
}