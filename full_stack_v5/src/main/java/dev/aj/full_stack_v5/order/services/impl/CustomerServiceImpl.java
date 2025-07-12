package dev.aj.full_stack_v5.order.services.impl;

import dev.aj.full_stack_v5.auth.domain.entities.User;
import dev.aj.full_stack_v5.auth.service.UserService;
import dev.aj.full_stack_v5.order.domain.dtos.CustomerDto;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import dev.aj.full_stack_v5.order.domain.mappers.CustomerMapper;
import dev.aj.full_stack_v5.order.repositories.CustomerRepository;
import dev.aj.full_stack_v5.order.services.CustomerService;
import io.jsonwebtoken.lang.Objects;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final CustomerMapper customerMapper;

    @Override
    public Customer createCustomer(CustomerDto customerDto) {

        User user = userService.getUserByTheUsername(customerDto.getUsername());
        
        Customer customer = customerMapper.customerDtoToCustomer(customerDto);
        customer.setUser(user);
        
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Long id, CustomerDto customerDto) {
        return customerRepository.findCustomerById(id)
                .map(customer -> patchCustomerFromCustomerDto(customer, customerDto))
                .map(customerRepository::save)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id: %s not found".formatted(id)));
    }

    private Customer patchCustomerFromCustomerDto(Customer customer, CustomerDto customerDto) {

        if (!Objects.isEmpty(customerDto.getFirstName())) {
            customer.setFirstName(customerDto.getFirstName());
        }
        if (!Objects.isEmpty(customerDto.getLastName())) {
            customer.setLastName(customerDto.getLastName());
        }
        if (!Objects.isEmpty(customerDto.getEmail())) {
            customer.setEmail(customerDto.getEmail());
        }
        if (!Objects.isEmpty(customerDto.getPhone())) {
            customer.setPhone(customerDto.getPhone());
        }
        if (!Objects.isEmpty(customerDto.getAddress())) {
            customer.setAddress(customerDto.getAddress());
        }

        return customer;
    }

    @Override
    public Customer getCustomerByUsername(String username) {
        return customerRepository.findCustomerByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Customer with username: %s not found".formatted(username)));
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.findCustomerById(id)
                .ifPresentOrElse(
                        customerRepository::delete,
                        () -> log.error("Unable to find Customer with id: {} to delete", id)
                );
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findCustomerById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with id: %s not found".formatted(id)));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
