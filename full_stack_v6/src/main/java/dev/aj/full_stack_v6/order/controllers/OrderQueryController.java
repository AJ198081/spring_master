package dev.aj.full_stack_v6.order.controllers;

import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderQueryController {

    private final OrderService orderService;

    @QueryMapping
    public String sayHello() {
        return "Hello World";
    }

    @QueryMapping
    public String sayHelloTo(@Argument("name") String name) {
        return "Hello %s".formatted(name);
    }

    @QueryMapping
    public int random() {
        return (int) (Math.random() * 100);
    }

    @QueryMapping
    public int randomIntegerBetween(@Argument("min") int min, @Argument("max") int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    @QueryMapping
    public List<Order> ordersForCustomer(@Argument("id") Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

}
