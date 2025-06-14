package dev.aj.full_stack_v5.order.services.impl;

import dev.aj.full_stack_v5.order.domain.entities.Customer;
import dev.aj.full_stack_v5.order.repositories.CustomerRepository;
import dev.aj.full_stack_v5.order.services.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findCustomerById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id: %s not found".formatted(id)));
    }

}
