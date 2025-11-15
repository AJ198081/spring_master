package dev.aj.full_stack_v6.clients.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepositoryImplementation<Customer, Long> {

    @Query("select c from Customer c where c.user.username = ?1")
    Optional<Customer> findCustomerByUsername(String username);

    Customer findCustomerByLastNameLike(String lastnamePattern);

}