package dev.aj.full_stack_v5.order.controllers;

import dev.aj.full_stack_v5.order.services.OrderService;
import dev.aj.full_stack_v5.order.domain.entities.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<Order> createOrder(@RequestParam Long customerId) {
        log.info("Creating order for customer with id: {}", customerId);
        return ResponseEntity.ok(orderService.createOrder(customerId));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId) {

        log.info("Getting orders for customer : {}", customerId);
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
    }


}
