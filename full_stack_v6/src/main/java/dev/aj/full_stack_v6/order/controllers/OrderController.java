package dev.aj.full_stack_v6.order.controllers;

import dev.aj.full_stack_v6.common.aspects.logging.MeasurePerformance;
import dev.aj.full_stack_v6.common.domain.dtos.OrderHistory;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.enums.OrderStatus;
import dev.aj.full_stack_v6.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${ORDER_API_PATH}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    @MeasurePerformance
    public ResponseEntity<HttpStatus> createOrder(@RequestParam("paymentIdentifier") UUID paymentIdentifier, Principal principal) {
        return ResponseEntity
                .created(URI.create("/".concat(orderService.placeOrder(paymentIdentifier, principal))))
                .build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable("orderId") UUID orderId, Principal principal) {
        return ResponseEntity.ok(orderService.getOrderById(orderId, principal));
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<List<OrderHistory>> getOrderHistoryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOrderHistory(id));
    }

    @GetMapping("/status/{orderStatus}")
    public ResponseEntity<List<Order>> findOrderByStatus(@PathVariable("orderStatus") OrderStatus orderStatus) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(orderStatus));
    }
}
