package dev.aj.full_stack_v6_kafka.common.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.modulith.NamedInterface;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedInterface("dtos")
public final class TransferRequestDto {
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
}
