package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.Customer;
import dev.aj.sdj_hibernate.domain.entities.Passport;

public interface CustomerManagementService {

    Customer persistACustomerAndPassport(Customer customer, Passport passport);

}
