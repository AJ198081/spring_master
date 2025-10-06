package dev.aj.full_stack_v6.common.domain.events;

import dev.aj.full_stack_v6.common.domain.events.dto.ShippingDetails;

import java.math.BigDecimal;

public record OrderPlacedEvent(String orderId,
                               Long customerId,
                               String firstName,
                               String lastName,
                               ShippingDetails shippingDetails,
                               BigDecimal orderTotal) {
}
