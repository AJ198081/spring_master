package dev.aj.full_stack_v6.clients.controllers;

import dev.aj.full_stack_v6.clients.CustomerService;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerQueryController {

    private final CustomerService customerService;

    @QueryMapping
    public List<Customer> allCustomers() {
        return customerService.getAllCustomers();
    }

    @QueryMapping
    public Customer customerById(@Argument("id") Long id) {
        return customerService.getCustomerById(id);
    }

    @QueryMapping
    public Customer customersLastnameLike(@Argument("lastName") String lastnamePattern) {
        return customerService.getCustomerByLastnameLike(lastnamePattern);
    }
}
