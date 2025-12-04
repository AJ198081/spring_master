package dev.aj.full_stack_v6.clients.controllers;

import dev.aj.full_stack_v6.clients.CustomerService;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${CUSTOMER_API_PATH}")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer (PUT operation)", responses = {
            @ApiResponse(description = "Customer updated successfully", responseCode = "202")
    })
    public ResponseEntity<Void> putCustomer(@PathVariable("id") Long id, @RequestBody Customer customer) {
        customerService.putCustomer(id, customer);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a customer (PATCH operation)", responses = {
            @ApiResponse(description = "Customer updated successfully", responseCode = "202")
    })
    public ResponseEntity<Void> patchCustomer(@PathVariable("id") Long id, @RequestBody Customer customer) {
        customerService.patchCustomer(id, customer);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<Customer> addAddresses(@PathVariable("id") Long id, @RequestBody List<Address> addresses) {
        return ResponseEntity.ok(customerService.addAddresses(id, addresses));
    }

    @PutMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Customer> updateAddress(@PathVariable("id") Long id,
                                                  @PathVariable("addressId") Long addressId,
                                                  @RequestBody Address address) {
        return ResponseEntity.ok(customerService.updateAddress(id, addressId, address));
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") Long id,
                                              @PathVariable("addressId") Long addressId) {
        customerService.deleteAddress(id, addressId);
        return ResponseEntity.ok().build();
    }
}
