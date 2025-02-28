package dev.aj.spring_modulith.order.entities;

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
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_inventory", schema = "modulith", indexes = {
        @Index(name = "idx_order_id", columnList = "order_id", unique = true),
})
public class OrderInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_inventory_seq_generator")
    @SequenceGenerator(name = "order_inventory_seq_generator", sequenceName = "order_inventory_seq", schema = "modulith", allocationSize = 5, initialValue = 100)
    private Long id;

    private UUID orderId;
    private UUID inventoryId;
    private int quantity;
    private BigDecimal totalQtyPrice;
}

