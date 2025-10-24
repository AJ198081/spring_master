package dev.aj.full_stack_v6_kafka.transfers;

import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import dev.aj.full_stack_v6_kafka.common.exceptions.TransferProcessingException;

import java.util.UUID;

public interface TransferService {

    void transferFunds(TransferRequestDto transferRequestDto, UUID messageId) throws TransferProcessingException;

}
