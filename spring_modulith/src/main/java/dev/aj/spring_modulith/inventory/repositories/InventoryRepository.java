package dev.aj.spring_modulith.inventory.repositories;

import dev.aj.spring_modulith.inventory.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findFirstByInventoryId(UUID inventoryId);


    Optional<Inventory> getInventoryByName(String productName);

    Optional<Inventory> findByNameIgnoreCase(String name);


    Optional<Inventory> getInventoryById(Long id);

    Optional<Inventory> getInventoryByInventoryId(UUID inventoryId);
}
