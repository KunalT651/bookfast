package com.bookfast.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GoogleCalendarConfig {

    @Value("${google.calendar.client.id}")
    private String clientId;

    @Value("${google.calendar.client.secret}")
    private String clientSecret;

    @Value("${google.calendar.redirect.uri}")
    private String redirectUri;

    @Value("${google.calendar.scope}")
    private String scope;

    @Value("${google.calendar.auth.uri}")
    private String authUri;

    @Value("${google.calendar.token.uri}")
    private String tokenUri;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Getters for configuration values
    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public String getAuthUri() {
        return authUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }
}
