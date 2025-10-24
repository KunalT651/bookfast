package com.bookfast.backend.resource.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    public StripeService() {
        // Paste your Stripe secret key here
        Stripe.apiKey = "sk_test_YOUR_SECRET_KEY";
    }

    public String createPaymentIntent(long amount, String currency) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amount)
            .setCurrency(currency)
            .build();
        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }
}
