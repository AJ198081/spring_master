package dev.aj.full_stack_v5.order.controllers;


import dev.aj.full_stack_v5.order.domain.dtos.CustomerDto;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import dev.aj.full_stack_v5.order.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/")
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDto customerDto) {
        log.info("Creating a customer with a username: {}", customerDto.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.createCustomer(customerDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        log.info("Getting customer with id: {}", id);
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    @GetMapping("/username/{username}")
    public ResponseEntity<Customer> getCustomerByUsername(@PathVariable String username) {
        log.info("Getting customer with username: {}", username);
        return ResponseEntity.ok(customerService.getCustomerByUsername(username));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@RequestBody CustomerDto customerDto, @PathVariable Long id) {
        log.info("Updating customer with id: {}", id);
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCustomerById(@PathVariable Long id) {
        log.info("Deleting customer with id: {}", id);
        customerService.deleteCustomer(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
