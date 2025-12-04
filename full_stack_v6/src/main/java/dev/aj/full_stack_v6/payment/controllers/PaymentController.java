package dev.aj.full_stack_v6.payment.controllers;

import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.entities.PaymentDetails;
import dev.aj.full_stack_v6.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("${PAYMENT_API_PATH}")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/card")
    @Operation(summary = "Process a card payment", responses = {
            @ApiResponse(responseCode = "201", description = "Payment Created, reference in the 'location' header")
    })
    public ResponseEntity<Void> processCardPayment(@RequestBody PaymentDetails paymentDetails, Principal principal) {
        return ResponseEntity
                .created(URI.create("/"
                                .concat(paymentService.processCardPayment(paymentDetails, principal))))
                .build();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("paymentId") UUID paymentId, Principal principal) {
        return ResponseEntity.ok(paymentService.getPaymentByPaymentId(paymentId, principal));
    }


}
