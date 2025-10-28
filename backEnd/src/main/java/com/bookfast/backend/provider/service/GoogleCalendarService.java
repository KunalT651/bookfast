package com.bookfast.backend.provider.service;

import com.bookfast.backend.common.config.GoogleCalendarConfig;
import com.bookfast.backend.common.model.CalendarToken;
import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.CalendarTokenRepository;
import com.bookfast.backend.common.repository.UserRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "BookFast Calendar Integration";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Autowired
    private GoogleCalendarConfig config;

    @Autowired
    private CalendarTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Validates if an email is a Gmail address
     */
    public boolean isValidGmailAddress(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailLower = email.toLowerCase().trim();
        return emailLower.endsWith("@gmail.com") || emailLower.endsWith("@googlemail.com");
    }

    /**
     * Generates Google OAuth authorization URL
     */
    public String generateAuthUrl(Long userId, String userEmail) {
        try {
            if (!isValidGmailAddress(userEmail)) {
                throw new IllegalArgumentException("Google Calendar integration requires a Gmail address");
            }

            GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
            details.setClientId(config.getClientId());
            details.setClientSecret(config.getClientSecret());

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setWeb(details);

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();

            return flow.newAuthorizationUrl()
                    .setRedirectUri(config.getRedirectUri())
                    .setState(userId.toString())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate auth URL: " + e.getMessage());
        }
    }

    /**
     * Exchanges authorization code for tokens and stores them
     */
    public String exchangeCodeForTokens(Long userId, String authCode) {
        try {
            GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
            details.setClientId(config.getClientId());
            details.setClientSecret(config.getClientSecret());

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setWeb(details);

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();

            TokenResponse tokenResponse = flow.newTokenRequest(authCode)
                    .setRedirectUri(config.getRedirectUri())
                    .execute();

            // Get user and update calendar connection status
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Store or update calendar token
            Optional<CalendarToken> existingToken = tokenRepository.findByUserAndIsActiveTrue(user);
            CalendarToken token = existingToken.orElse(new CalendarToken());
            
            token.setUser(user);
            token.setAccessToken(tokenResponse.getAccessToken());
            token.setRefreshToken(tokenResponse.getRefreshToken());
            token.setTokenType(tokenResponse.getTokenType());
            token.setExpiresIn(tokenResponse.getExpiresInSeconds());
            token.setExpiresAt(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresInSeconds()));
            token.setScope(String.join(",", SCOPES));
            token.setCalendarId("primary");
            token.setIsActive(true);
            token.setUpdatedAt(LocalDateTime.now());

            tokenRepository.save(token);

            // Update user calendar connection status
            user.setCalendarConnected(true);
            user.setCalendarEmail(user.getEmail());
            user.setGoogleCalendarId("primary");
            userRepository.save(user);

            return "Google Calendar connected successfully! Your calendar is now synced with BookFast.";
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange code for tokens: " + e.getMessage());
        }
    }

    /**
     * Creates a calendar event for a booking
     */
    public String createCalendarEvent(Long userId, String title, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CalendarToken token = tokenRepository.findValidTokenByUser(user)
                    .orElseThrow(() -> new RuntimeException("No valid calendar token found"));

            // Refresh token if expired
            if (token.isExpired()) {
                refreshAccessToken(token);
            }

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = createCredential(httpTransport, token);

            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Event event = new Event()
                    .setSummary(title)
                    .setDescription("BookFast Booking - " + title);

            // Convert LocalDateTime to Google Calendar format
            String startTimeStr = startTime.atZone(ZoneId.systemDefault()).toString();
            String endTimeStr = endTime.atZone(ZoneId.systemDefault()).toString();

            event.setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(startTimeStr)));
            event.setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(endTimeStr)));

            Event createdEvent = service.events().insert("primary", event).execute();
            
            System.out.println("📅 Google Calendar Event Created:");
            System.out.println("   Event ID: " + createdEvent.getId());
            System.out.println("   Title: " + title);
            System.out.println("   Start: " + startTime);
            System.out.println("   End: " + endTime);

            return createdEvent.getId();
        } catch (Exception e) {
            System.err.println("Failed to create calendar event: " + e.getMessage());
            throw new RuntimeException("Failed to create calendar event: " + e.getMessage());
        }
    }

    /**
     * Updates a calendar event
     */
    public void updateCalendarEvent(Long userId, String eventId, String title, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CalendarToken token = tokenRepository.findValidTokenByUser(user)
                    .orElseThrow(() -> new RuntimeException("No valid calendar token found"));

            if (token.isExpired()) {
                refreshAccessToken(token);
            }

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = createCredential(httpTransport, token);

            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Event event = service.events().get("primary", eventId).execute();
            event.setSummary(title);

            String startTimeStr = startTime.atZone(ZoneId.systemDefault()).toString();
            String endTimeStr = endTime.atZone(ZoneId.systemDefault()).toString();

            event.setStart(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(startTimeStr)));
            event.setEnd(new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(endTimeStr)));

            service.events().update("primary", eventId, event).execute();
            
            System.out.println("📅 Google Calendar Event Updated:");
            System.out.println("   Event ID: " + eventId);
            System.out.println("   Title: " + title);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update calendar event: " + e.getMessage());
        }
    }

    /**
     * Deletes a calendar event
     */
    public void deleteCalendarEvent(Long userId, String eventId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CalendarToken token = tokenRepository.findValidTokenByUser(user)
                    .orElseThrow(() -> new RuntimeException("No valid calendar token found"));

            if (token.isExpired()) {
                refreshAccessToken(token);
            }

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = createCredential(httpTransport, token);

            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            service.events().delete("primary", eventId).execute();
            
            System.out.println("📅 Google Calendar Event Deleted:");
            System.out.println("   Event ID: " + eventId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete calendar event: " + e.getMessage());
        }
    }

    /**
     * Disconnects user's Google Calendar
     */
    public void disconnectCalendar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Deactivate all tokens for this user
        List<CalendarToken> tokens = tokenRepository.findByUser(user);
        for (CalendarToken token : tokens) {
            token.setIsActive(false);
            token.setUpdatedAt(LocalDateTime.now());
        }
        tokenRepository.saveAll(tokens);

        // Update user status
        user.setCalendarConnected(false);
        user.setCalendarEmail(null);
        user.setGoogleCalendarId(null);
        userRepository.save(user);
    }

    /**
     * Checks if user has calendar connected
     */
    public boolean isCalendarConnected(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCalendarConnected() != null && user.getCalendarConnected();
    }

    private Credential createCredential(NetHttpTransport httpTransport, CalendarToken token) throws IOException {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(config.getClientId());
        details.setClientSecret(config.getClientSecret());

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        clientSecrets.setWeb(details);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        return flow.createAndStoreCredential(
                new TokenResponse()
                        .setAccessToken(token.getAccessToken())
                        .setRefreshToken(token.getRefreshToken())
                        .setExpiresInSeconds(token.getExpiresIn()),
                token.getUser().getId().toString());
    }

    private void refreshAccessToken(CalendarToken token) {
        // Implementation for token refresh would go here
        // For now, we'll just log that refresh is needed
        System.out.println("Token refresh needed for user: " + token.getUser().getEmail());
    }
}
