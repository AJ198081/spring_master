package dev.aj.full_stack_v5.order.repositories;

import dev.aj.full_stack_v5.order.domain.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOrderByCustomerId(Long customerId);
}
