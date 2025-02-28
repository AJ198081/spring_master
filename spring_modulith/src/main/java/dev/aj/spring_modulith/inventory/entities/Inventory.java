package dev.aj.spring_modulith.inventory.entities;

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
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventory", schema = "modulith", indexes = {
        @Index(name = "idx_inventory_name", columnList = "name", unique = true),
        @Index(name = "idx_inventory_id", columnList = "inventory_id", unique = true)
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq")
    @SequenceGenerator(name = "inventory_seq", sequenceName = "inventory_seq", schema = "modulith", allocationSize = 10, initialValue = 10000)
    private Long id;

    @Builder.Default
    private UUID inventoryId = UUID.randomUUID();

    @Column(unique = true)
    private String name;

    private String description;

    private int quantity;

    private BigDecimal price;

}
