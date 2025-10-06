package dev.aj.full_stack_v6.order.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Order;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepositoryImplementation<Order, Long> {

}