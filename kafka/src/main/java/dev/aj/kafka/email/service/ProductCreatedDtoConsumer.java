package dev.aj.kafka.email.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.kafka.product.domain.dto.ProductCreatedDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaHandler
    public void sendEmail(@Payload ProductCreatedDto productCreatedDto) {

        log.info("Received product created event {}", productCreatedDto);

        emailService.sendEmail("recipientEmail@gmail.com",
                "recipientName",
                "%s created".formatted(productCreatedDto.getName()),
                objectMapper.writeValueAsString(productCreatedDto));
    }
}
