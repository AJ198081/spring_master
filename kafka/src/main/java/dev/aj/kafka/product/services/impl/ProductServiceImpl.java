package dev.aj.kafka.product.services.impl;

import dev.aj.kafka.product.domain.dto.ProductCreateDto;
import dev.aj.kafka.product.domain.dto.ProductCreatedDto;
import dev.aj.kafka.product.domain.entities.Product;
import dev.aj.kafka.product.domain.mappers.ProductMapper;
import dev.aj.kafka.product.repositories.ProductRepository;
import dev.aj.kafka.product.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final KafkaTemplate<String, Object> customKafkaTemplate;
    private final Environment environment;

    @Override
    public ProductCreatedDto createProduct(ProductCreateDto productCreateDto) {

        log.info("Creating product {}", productCreateDto);
        Product persistedProduct = productRepository.save(productMapper.toEntity(productCreateDto));
        log.info("Created product with ID {}", persistedProduct.getId());

        ProductCreatedDto productCreatedDto = productMapper.toDto(persistedProduct);

        CompletableFuture<SendResult<String, Object>> productSendFuture = customKafkaTemplate.send(
                Objects.requireNonNull(environment.getProperty("product.created.event.topic.name")),
                productCreatedDto.getId().toString(),
                persistedProduct);

        // Send to kafka is still 'async', future.join() will block until the result is received
        productSendFuture.whenComplete((result, exception) -> {
            if (Objects.nonNull(exception)) {
                log.error("Error sending product-created event for Id: {} to Kafka", productCreatedDto.getId(), exception);
            } else {
                log.info("Product created event for id: {} successfully persisted to the Kafka Topic: {}, Partition: {}",
                        result.getProducerRecord().key(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition()
                );
            }
        });

        return productCreatedDto;
    }

    @Override
    public void deleteAllProducts() {
        log.info("Deleting all products");
    }

}
