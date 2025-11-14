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

    public void sendEmail(String to, String subject, String content) throws Exception {
        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            throw new IllegalStateException("SendGrid API key is not configured. Please set SENDGRID_API_KEY environment variable.");
        }
        
        System.out.println("[EmailService] Sending email to: " + to);
        System.out.println("[EmailService] Subject: " + subject);
        System.out.println("[EmailService] SendGrid API key configured: " + (sendGridApiKey != null && !sendGridApiKey.isEmpty()));
        
        Email fromEmail = new Email("noreply@bookfast.com");
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
        System.out.println("[EmailService] SendGrid API key configured: " + (sendGridApiKey != null && !sendGridApiKey.isEmpty()));
        
        Email fromEmail = new Email("noreply@bookfast.com");
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
        System.out.println("[EmailService] SendGrid response body: " + response.getBody());
        System.out.println("[EmailService] SendGrid response headers: " + response.getHeaders());
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            System.out.println("[EmailService] HTML email sent successfully to: " + to);
        } else {
            throw new Exception("SendGrid API returned status " + response.getStatusCode() + ": " + response.getBody());
        }
    }
}