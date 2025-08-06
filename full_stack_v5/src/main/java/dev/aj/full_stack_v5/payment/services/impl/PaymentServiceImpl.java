package dev.aj.full_stack_v5.payment.services.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import dev.aj.full_stack_v5.common.exception_handlers.exceptions.PaymentFailureException;
import dev.aj.full_stack_v5.payment.domain.dtos.PaymentRequest;
import dev.aj.full_stack_v5.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String createPaymentIntent(PaymentRequest paymentRequest) {

        try {
            return PaymentIntent.create(PaymentIntentCreateParams.builder()
                            .setAmount(paymentRequest.getAmount().longValue())
                            .setCurrency(paymentRequest.getCurrency())
                            .addPaymentMethodType("card")
                            .build())
                    .getClientSecret();
        } catch (StripeException e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            throw new PaymentFailureException(e.getMessage(), e);
        }
    }
}
