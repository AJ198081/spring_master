package dev.aj.full_stack_v5.product.repositories;

import dev.aj.full_stack_v5.order.domain.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findOrderItemByProductId(Long id);
}
