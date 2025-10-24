package dev.aj.full_stack_v6_kafka.transfers.controllers;

import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import dev.aj.full_stack_v6_kafka.common.exceptions.TransferProcessingException;
import dev.aj.full_stack_v6_kafka.transfers.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${TRANSFER_API_PATH}")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/")
    public ResponseEntity<HttpStatus> transferFunds(@RequestBody TransferRequestDto transferRequestDto, @RequestParam(required = false) UUID messageId) throws TransferProcessingException {
        transferService.transferFunds(transferRequestDto, messageId);
        return ResponseEntity.accepted().build();
    }

}
