package dev.aj.spring_modulith.order.services;

import dev.aj.spring_modulith.inventory.InventoryService;
import dev.aj.spring_modulith.inventory.dtos.InventoryEntityDto;
import dev.aj.spring_modulith.order.OrderService;
import dev.aj.spring_modulith.order.entities.Order;
import dev.aj.spring_modulith.order.repositories.OrderRepository;
import dev.aj.spring_modulith.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private ApplicationEventPublisher eventPublisher;
    private PaymentService paymentService;

    @Override
    public Order createOrder(Order order) {
        InventoryEntityDto inventoryDetails = inventoryService.getInventoryByProductName("product");
        inventoryService.reserveInventory(inventoryDetails.getInventoryId(), 2);
        return null;
    }
}
