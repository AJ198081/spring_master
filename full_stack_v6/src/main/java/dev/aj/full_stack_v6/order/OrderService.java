package dev.aj.full_stack_v6.order;

import dev.aj.full_stack_v6.common.domain.entities.Order;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.modulith.moments.DayHasPassed;

import java.security.Principal;
import java.util.UUID;

public interface OrderService {
    String placeOrder(UUID paymentIdentifier, Principal principal);

    Order getOrderById(UUID orderId, Principal principal);

    @ApplicationModuleListener
    void on(DayHasPassed dayHasPassed);
}
