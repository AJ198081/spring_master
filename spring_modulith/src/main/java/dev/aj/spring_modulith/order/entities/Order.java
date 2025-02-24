package dev.aj.spring_modulith.order.entities;

import dev.aj.spring_modulith.order.entities.types.Status;
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

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders", schema = "modulith", indexes = {
        @Index(name = "idx_order", columnList = "order_id", unique = true),
})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_generator")
    @SequenceGenerator(name = "order_seq_generator", sequenceName = "order_seq", allocationSize = 10, initialValue = 100000)
    private Long id;

    @Column(unique = true, nullable = false, columnDefinition = "uuid", length = 36, updatable = false)
    @Builder.Default
    private UUID orderId = UUID.randomUUID();

    private String customerName;

    private String customerEmail;

    @Builder.Default
    private Instant orderTime = Instant.now();

    @Builder.Default
//    @Enumerated(EnumType.STRING) //Either use @Enumerated or @Converter 'attribute converter', they are mutually exclusive
    private Status status = Status.OPEN;

}
