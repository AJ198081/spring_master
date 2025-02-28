package dev.aj.spring_modulith.order;

import dev.aj.spring_modulith.order.entities.Order;

public interface OrderService {

    Order createOrder(Order order);

}
