package com.bookfast.backend.resource.controller;

import org.springframework.web.bind.annotation.*;
import com.bookfast.backend.resource.model.Payment;
import com.bookfast.backend.resource.service.PaymentService;
import java.util.List;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:4200", "https://*.vercel.app"}, allowCredentials = "true")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        return service.createPayment(payment);
    }

    // Demo mode: Stripe PaymentIntent endpoint is disabled
    // @PostMapping("/create-intent")
    // public Map<String, String> createPaymentIntent(@RequestBody Map<String,
    // Object> payload) {
    // throw new UnsupportedOperationException("Stripe payment is disabled in demo
    // mode. Payment is handled by backend only.");
    // }

    @GetMapping("/booking/{bookingId}")
    public List<Payment> getPaymentsByBooking(@PathVariable Long bookingId) {
        return service.getPaymentsByBooking(bookingId);
    }
}
