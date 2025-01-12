package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.entities.Customer;
import dev.aj.sdj_hibernate.domain.entities.Passport;
import dev.aj.sdj_hibernate.domain.repositories.CustomerRepository;
import dev.aj.sdj_hibernate.domain.services.CustomerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerManagementServiceImpl implements CustomerManagementService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = false)
    @Override
    public Customer persistACustomerAndPassport(Customer customer, Passport passport) {
        passport.addCustomer(customer);
        return customerRepository.save(customer);
    }
}
