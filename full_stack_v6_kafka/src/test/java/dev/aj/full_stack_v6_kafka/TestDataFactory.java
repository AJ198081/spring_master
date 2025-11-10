package dev.aj.full_stack_v6_kafka;

import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Slf4j
public class TestDataFactory {

    @SuppressWarnings("unused")
    private final Faker faker;

    public Stream<TransferRequestDto> getStreamOfTransferRequestDtos() {
        return Stream.generate(() -> TransferRequestDto.builder()
                .fromAccountId(UUID.randomUUID())
                .toAccountId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(faker.random().nextLong(100L, 1000L)))
                .build());
    }

    public Stream<String> getStreamOfWords() {
        return Stream.generate(() -> faker.lorem().word());
    }
}
