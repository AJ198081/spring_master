package dev.aj.full_stack_v5.order.services;

import dev.aj.full_stack_v5.order.domain.dtos.CustomerDto;
import dev.aj.full_stack_v5.order.domain.entities.Customer;

import java.util.List;

public interface CustomerService {

    Customer createCustomer(CustomerDto customerDto);

    Customer updateCustomer(Long id, CustomerDto customerDto);

    Customer getCustomerByUsername(String username);

    void deleteCustomer(Long id);

    Customer getCustomerById(Long id);

    List<Customer> getAllCustomers();
}
