package dev.aj.full_stack_v6_kafka.common.domain.events;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawalEvent(UUID senderAccountId, UUID receiverAccountId, BigDecimal amount) {
}
