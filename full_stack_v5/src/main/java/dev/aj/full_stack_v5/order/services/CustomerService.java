package dev.aj.full_stack_v5.order.services;

import dev.aj.full_stack_v5.order.domain.entities.Customer;

public interface CustomerService {

    Customer getCustomerById(Long id);

}
