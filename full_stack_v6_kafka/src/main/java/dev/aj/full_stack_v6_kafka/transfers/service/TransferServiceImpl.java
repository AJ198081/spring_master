package dev.aj.full_stack_v6_kafka.transfers.service;

import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import dev.aj.full_stack_v6_kafka.common.domain.entities.TransferRequest;
import dev.aj.full_stack_v6_kafka.common.domain.events.DepositEvent;
import dev.aj.full_stack_v6_kafka.common.domain.events.WithdrawalEvent;
import dev.aj.full_stack_v6_kafka.common.domain.mappers.TransferRequestMapper;
import dev.aj.full_stack_v6_kafka.common.exceptions.TransferProcessingException;
import dev.aj.full_stack_v6_kafka.transfers.TransferService;
import dev.aj.full_stack_v6_kafka.transfers.repositories.TransferRequestRepository;
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
    private final TransferRequestMapper transferRequestMapper;
    private final TransferRequestRepository transferRequestRepository;
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
     * @implNote 'kafkaTransactionManager' can only manage Kafka transactions.
     * <p>To have a transaction wrap both database and kafka transaction, use a JpaTransactionManager.
     */
    @Override
    @Transactional(
//            transactionManager = "kafkaTransactionManager",
            transactionManager = "transactionManager",
            rollbackFor = TransferProcessingException.class,
            noRollbackFor = ResourceAccessException.class
    )
    public void transferFunds(TransferRequestDto transferRequestDto, @Nullable UUID messageId) throws TransferProcessingException {

        log.info("Producing transfer request: {}", transferRequestDto.toString());

        TransferRequest savedTransferRequest = transferRequestRepository.save(transferRequestMapper.transferRequestDtoToTransferRequest(transferRequestDto));

        if (messageId == null) {
            log.info("Message id is null, there shouldn't be any message for amount {}", transferRequestDto.getAmount());
            log.info("TransferRequest Id: {} should also be reverted.", savedTransferRequest.getId());

        }



        WithdrawalEvent withdrawalEvent = new WithdrawalEvent(transferRequestDto.getFromAccountId(), transferRequestDto.getToAccountId(), transferRequestDto.getAmount());
        DepositEvent depositEvent = new DepositEvent(transferRequestDto.getToAccountId(), transferRequestDto.getFromAccountId(), transferRequestDto.getAmount());

        ProducerRecord<Object, Object> withdrawalEventRecord = new ProducerRecord<>(
                environment.getRequiredProperty("kafka.topics.withdrawals"),
                transferRequestDto.getAmount().intValue() % 3,
                transferRequestDto.getFromAccountId().toString(),
                withdrawalEvent
        );

        if (Objects.nonNull(messageId)) {
            withdrawalEventRecord.headers().add("messageId", messageId.toString().getBytes());
        }
        kafkaOperations.send(withdrawalEventRecord);

        if (Objects.isNull(messageId)) {
            throw new TransferProcessingException("Message id is null");
        }

        ProducerRecord<Object, Object> depositEventRecord = new ProducerRecord<>(
                environment.getRequiredProperty("kafka.topics.deposits"),
                transferRequestDto.getAmount().intValue() % 3,
                transferRequestDto.getToAccountId().toString(),
                depositEvent
        );
        depositEventRecord.headers().add("messageId", messageId.toString().getBytes());

        kafkaOperations.send(depositEventRecord);
    }
}
