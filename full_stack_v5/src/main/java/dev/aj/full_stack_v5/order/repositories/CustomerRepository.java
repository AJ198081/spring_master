package dev.aj.full_stack_v5.order.repositories;

import dev.aj.full_stack_v5.order.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerById(Long id);
}
