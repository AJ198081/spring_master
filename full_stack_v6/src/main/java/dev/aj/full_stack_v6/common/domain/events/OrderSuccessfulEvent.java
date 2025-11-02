package dev.aj.full_stack_v6.common.domain.events;

import dev.aj.full_stack_v6.common.domain.events.dto.ShippingDetails;
import org.springframework.modulith.events.Externalized;

import java.math.BigDecimal;

@Externalized(target = "orders::#{customerId}")
public record OrderSuccessfulEvent(
        String orderId,
        Long customerId,
        String firstName,
        String lastName,
        ShippingDetails shippingDetails,
        BigDecimal orderTotal
) {
}
