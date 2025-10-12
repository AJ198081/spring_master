package dev.aj.full_stack_v6.payment;

import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.entities.PaymentDetails;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.moments.DayHasPassed;

import java.security.Principal;
import java.util.UUID;

public interface PaymentService {
    String processCardPayment(PaymentDetails paymentDetails, Principal principal);

    Payment getPaymentByPaymentId(UUID paymentId, Principal principal);

    @EventListener
    void on(DayHasPassed dayHasPassed);
}
