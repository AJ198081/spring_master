package dev.aj.kafka.email.service;

import dev.aj.kafka.product.domain.dto.ProductCreatedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@KafkaListener(
        topics = "${product.created.event.topic.name}",
        id = "productCreatedDtoConsumer",
        groupId = "productCreatedDto-consumer-group",
        autoStartup = "true"
)
@Slf4j
public class ProductCreatedDtoConsumer {

    @KafkaHandler
    public void sendEmail(@Payload ProductCreatedDto productCreatedDto) {
       /* if (productCreatedDto.getId() % 2 == 0) {
            throw new NotRetryableException("Throwing custom exception for testing for product ID %d".formatted(productCreatedDto.getId()));
        }*/

        log.info("Received product created event {}", productCreatedDto);
    }
}
