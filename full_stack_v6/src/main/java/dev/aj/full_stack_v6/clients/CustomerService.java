package dev.aj.full_stack_v6.clients;

import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void putCustomer(Long id, Customer customer);
    void patchCustomer(Long id, Customer customer);

    Customer addAddresses(Long customerId, List<Address> addresses);
    Customer updateAddress(Long customerId, Long addressId, Address address);
    void deleteAddress(Long customerId, Long addressId);

    Customer getCustomerByUserName(String name);

    Customer getCustomerByLastnameLike(String lastnamePattern);
}
