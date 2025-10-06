package dev.aj.full_stack_v6.order.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Order;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepositoryImplementation<Order, Long> {

    Optional<Order> findByOrderId(UUID orderId);
}