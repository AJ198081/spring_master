package dev.aj.full_stack_v6_kafka.transfers.service;

import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import dev.aj.full_stack_v6_kafka.common.domain.entities.TransferRequest;
import dev.aj.full_stack_v6_kafka.common.domain.events.DepositEvent;
import dev.aj.full_stack_v6_kafka.common.domain.events.WithdrawalEvent;
import dev.aj.full_stack_v6_kafka.transfers.TransferService;
import dev.aj.full_stack_v6_kafka.transfers.repositories.TransferRequestRepository;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(count = 3, partitions = 3, controlledShutdown = true)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "kafka.consumer.groupId=test-consumer"
        })
class TransferServiceImplTest {

    @Autowired
    private TransferService transferService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private TransferRequestRepository transferRequestRepository;

    @Autowired
    private Environment environment;

    private KafkaMessageListenerContainer<String, WithdrawalEvent> withdrawalEventListenerContainer;
    private BlockingQueue<ConsumerRecord<String, WithdrawalEvent>> withdrawalEventQueue;

    private KafkaMessageListenerContainer<String, DepositEvent> depositEventListenerContainer;
    private BlockingQueue<ConsumerRecord<String, DepositEvent>> depositEventQueue;

    @BeforeAll
    void beforeAll() {
        DefaultKafkaConsumerFactory<String, Object> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(getKafkaMessageHeaders());

        withdrawalEventQueue = new LinkedBlockingQueue<>();
        depositEventQueue = new LinkedBlockingQueue<>();

        ContainerProperties withdrawalContainerProperties = new ContainerProperties(environment.getProperty("kafka.topics.withdrawals"));
        withdrawalEventListenerContainer = new KafkaMessageListenerContainer<>(defaultKafkaConsumerFactory, withdrawalContainerProperties);
        withdrawalEventListenerContainer.setupMessageListener((MessageListener<String, WithdrawalEvent>) withdrawalEventQueue::add);
        withdrawalEventListenerContainer.start();
        ContainerTestUtils.waitForAssignment(withdrawalEventListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());

        ContainerProperties depositContainerProperties = new ContainerProperties(environment.getProperty("kafka.topics.deposits"));
        depositEventListenerContainer = new KafkaMessageListenerContainer<>(defaultKafkaConsumerFactory, depositContainerProperties);
        depositEventListenerContainer.setupMessageListener((MessageListener<String, DepositEvent>) depositEventQueue::add);
        depositEventListenerContainer.start();
        ContainerTestUtils.waitForAssignment(depositEventListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());

        Mockito.when(transferRequestRepository.save(Mockito.any(TransferRequest.class)))
                .thenAnswer(invocation -> {
                    TransferRequest transferRequestObject = invocation.getArgument(0);
                    return TransferRequest.builder()
                            .id(45L)
                            .fromAccountId(transferRequestObject.getFromAccountId())
                            .toAccountId(transferRequestObject.getToAccountId())
                            .amount(transferRequestObject.getAmount())
                            .build();
                });
    }

    @AfterAll
    void afterAll() {
        withdrawalEventListenerContainer.stop();
        depositEventListenerContainer.stop();
    }

    @SneakyThrows
    @Test
    void transferFunds_WhenValid_SuccessfullySendsKafkaMessage() {

        TransferRequestDto transferRequest = TransferRequestDto.builder()
                .fromAccountId(UUID.randomUUID())
                .toAccountId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(4321L))
                .build();

        transferService.transferFunds(transferRequest, UUID.randomUUID());

        ConsumerRecord<String, WithdrawalEvent> withdrawalEvent = withdrawalEventQueue.poll(3000, TimeUnit.MILLISECONDS);
        assertThat(withdrawalEvent)
                .isNotNull()
                .extracting(ConsumerRecord::value)
                .isNotNull()
                .satisfies(wEvent -> {
                    assertThat(wEvent.senderAccountId()).isEqualTo(transferRequest.getFromAccountId());
                    assertThat(wEvent.receiverAccountId()).isEqualTo(transferRequest.getToAccountId());
                    assertThat(wEvent.amount()).isEqualTo(transferRequest.getAmount());
                });

        ConsumerRecord<String, DepositEvent> depositEvent = depositEventQueue.poll(3000, TimeUnit.MILLISECONDS);
        assertThat(depositEvent)
                .isNotNull()
                .extracting(ConsumerRecord::value)
                .isNotNull()
                .satisfies(dEvent -> {
                    assertThat(dEvent.senderAccountId()).isEqualTo(transferRequest.getFromAccountId());
                    assertThat(dEvent.receiverAccountId()).isEqualTo(transferRequest.getToAccountId());
                    assertThat(dEvent.amount()).isEqualTo(transferRequest.getAmount());
                });

        Mockito.verify(transferRequestRepository, Mockito.times(1))
                .save(Mockito.any(TransferRequest.class));
    }

    private Map<String, Object> getKafkaMessageHeaders() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("kafka.consumer.groupId", "test-consumer"),
                JsonDeserializer.TRUSTED_PACKAGES, "dev.aj.full_stack_v6_kafka.common.domain.events",
                JsonDeserializer.TYPE_MAPPINGS, """
                        withdrawalEvent:dev.aj.full_stack_v6_kafka.common.domain.events.WithdrawalEvent,
                        depositEvent:dev.aj.full_stack_v6_kafka.common.domain.events.DepositEvent
                        """,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );
    }
}