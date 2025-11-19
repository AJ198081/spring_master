package dev.aj.full_stack_v6.clients.controllers;

import dev.aj.full_stack_v6.clients.CustomerService;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomerQueryController {

    private final CustomerService customerService;

    @QueryMapping
    public List<Customer> allCustomers(DataFetchingEnvironment environment) {
        log.info("AllCustomers invoked with fields: {}", environment.getSelectionSet().toString());
        return customerService.getAllCustomers();
    }

    @QueryMapping
    public Customer customerById(@Argument("id") Long id, DataFetchingFieldSelectionSet selectionSet) {
        log.info("CustomerById: {}, with fields: {}", id, selectionSet.toString());
        return customerService.getCustomerById(id);
    }

    @QueryMapping
    public Customer customersLastnameLike(@Argument("lastName") String lastnamePattern) {
        return customerService.getCustomerByLastnameLike(lastnamePattern);
    }

    /**
     * Schema mapping for retrieving the customer's first name.
     * Spring GraphQL automatically infers the parent type (Customer) from the method parameter.
     *
     * <ul>From the @SchemaMapping(type="Customer", field="firstName") annotation - takes precedence</ul>
     * <ul>From the Parameter (@NonNull Customer) and method name -firstName</ul>
     *
     * @param customer The parent Customer object from which to extract the first name
     * @return The first name of the customer
     */
    @SchemaMapping(typeName = "Customer", field = "firstName")
    public String firstName(@NonNull Customer customer) {
        return customer.getFirstName().toUpperCase();
    }

    @SchemaMapping(typeName = "Customer", field = "addresses")
    public List<Address> addresses(@NonNull Customer customer) {
        if (!customer.getAddresses().isEmpty()) {
            return customer.getAddresses()
                    .stream()
                    .peek(address -> address.setStreet(address.getStreet().toUpperCase()))
                    .toList();
        }
        return null;
    }

 /*   @BatchMapping(typeName = "Customer")
    public List<Address> addresses(@NonNull Customer customer) {
        return customer.getAddresses();
    }*/
}
