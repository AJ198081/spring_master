package dev.aj.full_stack_v5.payment.controllers;

import dev.aj.full_stack_v5.payment.domain.dtos.PaymentRequest;
import dev.aj.full_stack_v5.payment.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/createPaymentIntent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(paymentRequest));
    }

}
