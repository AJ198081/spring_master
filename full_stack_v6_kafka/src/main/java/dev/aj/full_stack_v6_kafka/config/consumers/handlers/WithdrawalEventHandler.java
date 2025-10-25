package dev.aj.full_stack_v6_kafka.config.consumers.handlers;

import dev.aj.full_stack_v6_kafka.common.domain.events.WithdrawalEvent;
import dev.aj.full_stack_v6_kafka.common.exceptions.TransferProcessingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@KafkaListener(topics = "${kafka.topics.withdrawals}")
@Slf4j
public class WithdrawalEventHandler {

    private final ConcurrentHashMap<UUID, WithdrawalEvent> withdrawalEventMap = new ConcurrentHashMap<>();

    // You decide what message part you want to consume and annotate the parameters accordingly
    @SneakyThrows
    @KafkaHandler
    public void handleWithdrawalEvent(
            @Payload WithdrawalEvent withdrawalEvent,
            @Header(value = "messageId", required = false) String messageId,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partitionId) {

        if (messageId == null) {
            throw new TransferProcessingException("Message id is null");
        }

        if (withdrawalEventMap.putIfAbsent(UUID.fromString(messageId), withdrawalEvent) != null) {
            log.warn("Duplicate WithdrawalEvent with id {} from partition {}: {}", messageId, partitionId, withdrawalEvent);
            return;
        }

        log.info("Received request to withdraw with id {}, from partition {} is being handled here: {}", messageId, partitionId, withdrawalEvent);
    }
}
