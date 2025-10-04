package dev.aj.full_stack_v6.customer.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Customer;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepositoryImplementation<Customer, Long> {
}