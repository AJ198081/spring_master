package dev.aj.full_stack_v6.order.repositories;

import dev.aj.full_stack_v6.common.domain.entities.OrderItem;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface OrderItemRepository extends JpaRepositoryImplementation<OrderItem, Long> {
}