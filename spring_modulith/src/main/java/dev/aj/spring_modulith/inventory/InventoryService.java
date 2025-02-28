package dev.aj.spring_modulith.inventory;

import dev.aj.spring_modulith.inventory.dtos.InventoryEntityDto;

import java.util.UUID;

public interface InventoryService {

    InventoryEntityDto getInventoryByProductName(String productName);

    void reserveInventory(UUID inventoryId, int quantity);

}
