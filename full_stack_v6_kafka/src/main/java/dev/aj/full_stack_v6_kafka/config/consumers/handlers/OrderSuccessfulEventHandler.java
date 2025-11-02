package dev.aj.full_stack_v6_kafka.config.consumers.handlers;

import dev.aj.full_stack_v6.common.domain.events.OrderSuccessfulEvent;
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
public class OrderSuccessfulEventHandler {

    @KafkaHandler
    public void handleOrderSuccessfulEvent(
            @Payload OrderSuccessfulEvent orderSuccessfulEvent,
            @Header(value = "messageId", required = false) String messageId,
            @Header(KafkaHeaders.RECEIVED_PARTITION) String partitionId
    ) {
        log.info("OrderSuccessfulEvent with id {}, from partition {} is being handled here: {}", messageId, partitionId, orderSuccessfulEvent);
    }
}
