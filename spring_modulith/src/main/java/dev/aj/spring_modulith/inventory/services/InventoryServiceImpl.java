package dev.aj.spring_modulith.inventory.services;

import dev.aj.spring_modulith.inventory.InventoryService;
import dev.aj.spring_modulith.inventory.dtos.InventoryEntityDto;
import dev.aj.spring_modulith.inventory.entities.Inventory;
import dev.aj.spring_modulith.inventory.entities.mappers.InventoryMapper;
import dev.aj.spring_modulith.inventory.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public InventoryEntityDto getInventoryByProductName(String productName) {
        return inventoryMapper.entityToDto(inventoryRepository.findByNameIgnoreCase(productName).orElseThrow(() -> new NoSuchElementException("No inventory found for product Name: " + productName)));
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void reserveInventory(UUID inventoryId, int quantity) {
        Inventory inventoryItem = inventoryRepository.getInventoryByInventoryId(inventoryId).orElseThrow(() -> new NoSuchElementException("No inventory found for ID: " + inventoryId));
        inventoryItem.setQuantity(inventoryItem.getQuantity() - quantity);
        inventoryRepository.save(inventoryItem);
    }
}
