package dev.aj.kafka;

import dev.aj.kafka.config.KafkaConfig;
import dev.aj.kafka.product.domain.dto.ProductCreateDto;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
@Slf4j
@Import(value = {KafkaConfig.class})
public class TestData {

    private final Faker faker;
    private final KafkaConfig kafkaConfig;
    private final Environment environment;

    @Value("${password.special-characters: '!@#$%^'}")
    private char[] specialCharacters;


    public Stream<ProductCreateDto> streamOfCreateProductDtos() {
        return Stream.generate(() ->
                ProductCreateDto.builder()
                        .name(faker.commerce().productName())
                        .description(faker.lorem().sentence(50))
                        .price(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000)))
                        .quantity(faker.number().numberBetween(1, 100))
                        .build()
        );
    }


    private @Size(min = 8, message = "Password must be at least eight characters") String getValidPassword() {

        String password = faker.internet().password(8, 50, true, true, true);

        if (!StringUtils.containsAny(password, specialCharacters)) {
            password = password.substring(0, password.length() - 1)
                    .concat(String.valueOf(specialCharacters[faker.random().nextInt(specialCharacters.length)]));
        }

        return password;
    }

    @PostConstruct
    public void init() {

        String productCreatedTopic = environment.getProperty("product.created.event.topic.name");

        try (AdminClient adminClient = KafkaAdminClient.create(kafkaConfig.getKafkaProperties())) {
            Set<String> topics = adminClient.listTopics().names().get();
            if (!topics.contains(productCreatedTopic)) {
                NewTopic newTopic = kafkaConfig.createTopic(productCreatedTopic);
                Void topicCreationResult = adminClient.createTopics(Collections.singleton(newTopic))
                        .all()
                        .get();
                log.info("Kafka Topic {} Created", newTopic.name());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error creating Kafka topic: {}", e);
        }
    }

}
