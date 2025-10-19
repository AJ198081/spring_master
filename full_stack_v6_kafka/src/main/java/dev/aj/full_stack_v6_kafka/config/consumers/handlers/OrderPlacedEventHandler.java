package dev.aj.full_stack_v6_kafka.config.consumers.handlers;

import dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${kafka.topics.orders}")
@Slf4j
public class OrderPlacedEventHandler {

    // You decide what message part you want to consumer, and annotate the parameters accordingly
    @KafkaHandler
    public void handleOrderPlacedEvent(
            @Payload OrderPlacedEvent orderPlacedEvent,
            @Header(value = "messageId", required = false) String messageId,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partitionId) {

        if (messageId == null) {
            messageId = "Unknown";
        }

        log.info("OrderPlacedEvent with id {}, from partition {} is being handled here: {}", messageId, partitionId, orderPlacedEvent);
    }
}
