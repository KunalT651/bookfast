package com.bookfast.backend.common.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Value("${sendgrid.api-key:}")
    private String sendGridApiKey;

    @Bean
    public SendGrid sendGrid() {
        // Handle empty API key gracefully - SendGrid will still be created
        // EmailService handles missing key by checking before sending
        return new SendGrid(sendGridApiKey != null && !sendGridApiKey.isEmpty() ? sendGridApiKey : "dummy-key-for-local-dev");
    }
}