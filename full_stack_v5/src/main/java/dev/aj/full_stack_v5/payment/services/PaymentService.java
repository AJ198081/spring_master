package dev.aj.full_stack_v5.payment.services;

import dev.aj.full_stack_v5.payment.domain.dtos.PaymentRequest;

public interface PaymentService {
    String createPaymentIntent(PaymentRequest paymentRequest);
}
