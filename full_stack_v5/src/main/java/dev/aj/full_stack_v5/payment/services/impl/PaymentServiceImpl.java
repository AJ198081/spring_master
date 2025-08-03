package dev.aj.full_stack_v5.payment.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import dev.aj.full_stack_v5.common.exception_handlers.exceptions.PaymentFailureException;
import dev.aj.full_stack_v5.payment.domain.dtos.PaymentRequest;
import dev.aj.full_stack_v5.payment.services.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final Environment environment;

    @Override
    public String createPaymentIntent(PaymentRequest paymentRequest) {

        try {
          return PaymentIntent.create(PaymentIntentCreateParams.builder()
                            .setAmount(paymentRequest.getAmountToBeCharged().longValue())
                            .setCurrency(paymentRequest.getCurrency())
                            .addPaymentMethodType("card")
                    .build())
                    .getClientSecret();
        } catch (StripeException e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            throw new PaymentFailureException(e.getMessage(), e);
        }
    }

    @PostConstruct
    public void init() {
        String stripeApiKey = environment.getProperty("stripe.secret.key");
        if (stripeApiKey == null || stripeApiKey.trim().isEmpty()) {
            log.warn("Stripe API key not found in environment properties");
            return;
        }
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe API key configured successfully");

    }
}
