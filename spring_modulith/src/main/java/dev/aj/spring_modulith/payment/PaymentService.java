package dev.aj.spring_modulith.payment;

import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    void processPayment(String paymentId);
}
