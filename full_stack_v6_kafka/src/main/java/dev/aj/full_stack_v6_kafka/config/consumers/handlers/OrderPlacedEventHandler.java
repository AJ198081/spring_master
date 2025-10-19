package dev.aj.full_stack_v6_kafka.config.consumers.handlers;

import dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${kafka.topics.orders}")
@Slf4j
public class OrderPlacedEventHandler {

    @KafkaHandler
    public void handleOrderPlacedEvent(OrderPlacedEvent orderPlacedEvent) {
        log.info("OrderPlacedEvent is being handled here: {}", orderPlacedEvent);
    }

}
