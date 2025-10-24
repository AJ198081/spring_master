package dev.aj.full_stack_v6_kafka.transfers.service;

import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import dev.aj.full_stack_v6_kafka.common.domain.events.DepositEvent;
import dev.aj.full_stack_v6_kafka.common.domain.events.WithdrawalEvent;
import dev.aj.full_stack_v6_kafka.common.exceptions.TransferProcessingException;
import dev.aj.full_stack_v6_kafka.transfers.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final KafkaOperations<Object, Object> kafkaOperations;
    private final Environment environment;

    /**
     * Transfers funds between accounts using Kafka events.
     *
     * @param transferRequestDto the transfer request containing source account, destination account, and amount
     * @apiNote By default, Spring's Transaction Manager only rolls back transactions
     * for unchecked exceptions (RuntimeException and its subclasses) and errors.
     * Checked exceptions do not trigger a rollback unless explicitly configured, using rollbackFor.
     * Specific exceptions, not critical to the transfer flow, might be ignored by using noRollbackFor.
     * <p><u>Note:</u>Additionally, ensure to set Kafka Consumer Isolation Level to READ_COMMITTED.
     */
    @Override
    @Transactional(
            transactionManager = "kafkaTransactionManager",
            rollbackFor = TransferProcessingException.class,
            noRollbackFor = ResourceAccessException.class
    )
    public void transferFunds(TransferRequestDto transferRequestDto, @Nullable UUID messageId) throws TransferProcessingException {
        WithdrawalEvent withdrawalEvent = new WithdrawalEvent(transferRequestDto.getFromAccountId(), transferRequestDto.getToAccountId(), transferRequestDto.getAmount());
        DepositEvent depositEvent = new DepositEvent(transferRequestDto.getToAccountId(), transferRequestDto.getFromAccountId(), transferRequestDto.getAmount());

        if (Objects.isNull(messageId)) {
            throw new TransferProcessingException("Message id is null");
        }

        ProducerRecord<Object, Object> withdrawalEventRecord = new ProducerRecord<>(
                environment.getRequiredProperty("kafka.topics.withdrawals"),
                withdrawalEvent
        );

        kafkaOperations.send(withdrawalEventRecord);

        ProducerRecord<Object, Object> depositEventRecord = new ProducerRecord<>(
                environment.getRequiredProperty("kafka.topics.withdrawals"),
                depositEvent
        );
        depositEventRecord.headers().add("messageId", messageId.toString().getBytes());

        kafkaOperations.send(depositEventRecord);
    }
}
