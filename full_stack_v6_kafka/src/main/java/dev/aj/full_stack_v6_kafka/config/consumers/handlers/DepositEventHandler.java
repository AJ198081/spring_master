package dev.aj.full_stack_v6_kafka.config.consumers.handlers;

import dev.aj.full_stack_v6_kafka.common.domain.events.DepositEvent;
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
@KafkaListener(topics = "${kafka.topics.deposits}")
@Slf4j
public class DepositEventHandler {

    private final ConcurrentHashMap<UUID, DepositEvent> depositEventMap = new ConcurrentHashMap<>();

    // You decide what message part you want to consumer, and annotate the parameters accordingly
    @KafkaHandler
    public void handleDepositEvent(
            @Payload DepositEvent depositEvent,
            @Header(value = "messageId", required = false) String messageId,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partitionId) {

        if (messageId == null) {
            messageId = "Unknown";
            log.warn("Message id is null for DepositEvent with id {} from partition {}", messageId, partitionId);
            return;
        }

        if (depositEventMap.putIfAbsent(UUID.fromString(messageId), depositEvent) != null) {
            log.warn("Duplicate DepositEvent with id {} from partition {}: {}", messageId, partitionId, depositEvent);
            return;
        }

        log.info("DepositEvent with id {}, from partition {} is being handled here: {}", messageId, partitionId, depositEvent);
    }
}
