package com.bookfast.backend.resource.service;

import org.springframework.stereotype.Service;
import com.bookfast.backend.resource.model.Payment;
import com.bookfast.backend.resource.repository.PaymentRepository;
import java.util.List;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment createPayment(Payment payment) {
        return repository.save(payment);
    }

    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return repository.findByBookingId(bookingId);
    }

    // Demo mode: Stripe PaymentIntent creation is disabled
    // public String createStripePaymentIntent(int amount) {
    // throw new UnsupportedOperationException("Stripe payment is disabled in demo
    // mode. Payment is handled by backend only.");
    // }
}
