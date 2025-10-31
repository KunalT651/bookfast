package com.bookfast.backend.common.notification;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;
    
    @Value("${sendgrid.sender.email:noreply@bookfast.com}")
    private String senderEmail;
    
    @Value("${sendgrid.sender.name:BookFast}")
    private String senderName;

    public void sendEmail(String to, String subject, String content) throws Exception {
        Email fromEmail = new Email(senderEmail, senderName);
        Email toEmail = new Email(to);
        Content emailContent = new Content("text/plain", content);
        Mail mail = new Mail(fromEmail, subject, toEmail, emailContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            logger.info("Email sent successfully to: {}", to);
        } else {
            logger.warn("Email sending returned status {}: {}", response.getStatusCode(), response.getBody());
        }
    }

    /**
     * Send HTML email
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws Exception {
        Email fromEmail = new Email(senderEmail, senderName);
        Email toEmail = new Email(to);
        Content htmlEmailContent = new Content("text/html", htmlContent);
        Mail mail = new Mail(fromEmail, subject, toEmail, htmlEmailContent);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            logger.info("HTML email sent successfully to: {}", to);
        } else {
            logger.warn("HTML email sending returned status {}: {}", response.getStatusCode(), response.getBody());
        }
    }

    /**
     * Send welcome email to newly registered customer
     */
    public void sendWelcomeEmail(String to, String firstName, String lastName) {
        try {
            String subject = "Welcome to BookFast! 🎉";
            String htmlContent = buildWelcomeEmailHtml(firstName, lastName);
            sendHtmlEmail(to, subject, htmlContent);
            logger.info("Welcome email sent successfully to: {}", to);
        } catch (Exception e) {
            // Log error but don't throw - registration should not fail if email fails
            logger.error("Failed to send welcome email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Build HTML content for welcome email
     */
    private String buildWelcomeEmailHtml(String firstName, String lastName) {
        // Handle null values safely
        String safeFirstName = (firstName != null && !firstName.trim().isEmpty()) ? firstName.trim() : "there";
        String safeLastName = (lastName != null && !lastName.trim().isEmpty()) ? lastName.trim() : "";
        String fullName = safeFirstName + (!safeLastName.isEmpty() ? " " + safeLastName : "");
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".button { display: inline-block; padding: 12px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                ".footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"header\">" +
                "<h1>Welcome to BookFast! 🎉</h1>" +
                "</div>" +
                "<div class=\"content\">" +
                "<p>Hi " + fullName + ",</p>" +
                "<p>Thank you for joining BookFast! We're thrilled to have you on board.</p>" +
                "<p>BookFast is your one-stop platform for booking services and resources. Whether you're looking for professional services, appointments, or resources, we've got you covered.</p>" +
                "<p>Here's what you can do now:</p>" +
                "<ul>" +
                "<li>📅 Browse and book available services</li>" +
                "<li>🔍 Search for providers in your area</li>" +
                "<li>⭐ Leave reviews and ratings</li>" +
                "<li>💼 Manage your bookings easily</li>" +
                "</ul>" +
                "<p style=\"text-align: center;\">" +
                "<a href=\"http://localhost:4200\" class=\"button\">Get Started</a>" +
                "</p>" +
                "<p>If you have any questions or need assistance, feel free to reach out to our support team.</p>" +
                "<p>Happy booking!<br>The BookFast Team</p>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>© 2024 BookFast. All rights reserved.</p>" +
                "<p>This is an automated email. Please do not reply to this message.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}