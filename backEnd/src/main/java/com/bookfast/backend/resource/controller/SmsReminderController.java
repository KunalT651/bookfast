package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.bookfast.backend.resource.model.Booking;
import com.bookfast.backend.resource.repository.BookingRepository;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api/sms")
@CrossOrigin(origins = "http://localhost:4200")
public class SmsReminderController {
    @Autowired
    private BookingRepository bookingRepository;

    // For demo: Use free SMS API (e.g., textbelt)
    @PostMapping("/send")
    public String sendSmsReminder(@RequestBody SmsRequest request) throws Exception {
        String phone = request.getPhone();
        String message = request.getMessage();
        String apiKey = "textbelt"; // Free API key for textbelt demo
        String urlStr = "https://textbelt.com/text?phone=" + URLEncoder.encode(phone, "UTF-8") + "&message="
                + URLEncoder.encode(message, "UTF-8") + "&key=" + apiKey;
        URL url = new java.net.URI(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        int responseCode = conn.getResponseCode();
        return responseCode == 200 ? "SMS sent" : "Failed to send SMS";
    }

    public static class SmsRequest {
        private String phone;
        private String message;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
