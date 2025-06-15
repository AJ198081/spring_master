package dev.aj.full_stack_v5.order.repositories;

import dev.aj.full_stack_v5.auth.domain.entities.User;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerById(Long id);

    List<Customer> user(User user);

    @Query("""
            select c from Customer c
                        left join User u on u.id = c.user.id
                        where u.username = :username
            """)
    Optional<Customer> findCustomerByUsername(@Param("username") String username);
}
