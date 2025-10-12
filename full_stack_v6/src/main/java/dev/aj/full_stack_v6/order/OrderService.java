package dev.aj.full_stack_v6.order;

import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent;
import org.springframework.context.event.EventListener;

import java.security.Principal;
import java.util.UUID;

public interface OrderService {
    String placeOrder(UUID paymentIdentifier, Principal principal);

    Order getOrderById(UUID orderId, Principal principal);

    @EventListener
    void on(PaymentSuccessfulEvent paymentSuccessfulEvent);
}
