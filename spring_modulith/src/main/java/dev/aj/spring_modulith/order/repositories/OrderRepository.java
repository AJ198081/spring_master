package dev.aj.spring_modulith.order.repositories;

import dev.aj.spring_modulith.order.entities.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByOrderId(UUID orderId);
}
