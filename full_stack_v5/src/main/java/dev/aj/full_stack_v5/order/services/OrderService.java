package dev.aj.full_stack_v5.order.services;

import dev.aj.full_stack_v5.order.domain.entities.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Long customerId);

    List<Order> getCustomerOrders(Long customerId);
}
