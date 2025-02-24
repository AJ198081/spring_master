package dev.aj.spring_modulith.inventory.repositories;

import dev.aj.spring_modulith.inventory.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {



}
