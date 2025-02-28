package dev.aj.spring_modulith.payment.entities;

import dev.aj.spring_modulith.payment.entities.types.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment", schema = "modulith", indexes = {
        @Index(name = "idx_payment_id", columnList = "payment_id", unique = true)
})
public class Payment {

    @Id
    @GeneratedValue(generator = "payment_seq_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "payment_seq_generator", sequenceName = "payment_seq", schema = "modulith", allocationSize = 5, initialValue = 10000)
    private Long id;

    @Builder.Default
    @Column(unique = true, updatable = false, insertable = true, columnDefinition = "uuid", length = 36)
    private UUID paymentId = UUID.randomUUID();

    private UUID orderId;

    private BigDecimal amount;

    @Builder.Default
    private Instant paymentTime = Instant.now();

    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
}
