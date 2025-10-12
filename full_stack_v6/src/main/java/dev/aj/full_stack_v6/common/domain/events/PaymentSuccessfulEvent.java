package dev.aj.full_stack_v6.common.domain.events;

import java.util.UUID;

public record PaymentSuccessfulEvent(UUID paymentIdentifier, UUID orderId) {
}
