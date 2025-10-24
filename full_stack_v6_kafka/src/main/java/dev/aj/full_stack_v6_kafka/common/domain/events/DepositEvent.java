package dev.aj.full_stack_v6_kafka.common.domain.events;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositEvent(UUID receiverAccountId, UUID senderAccountId, BigDecimal amount) {
}
