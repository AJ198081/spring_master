package dev.aj.full_stack_v6_kafka.common.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transfer_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(name = "from_account_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID toAccountId;

    @Column(name = "amount", nullable = false)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private BigDecimal amount;

}
