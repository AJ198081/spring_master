package dev.aj.full_stack_v6.clients.services;

import dev.aj.full_stack_v6.clients.CustomerService;
import dev.aj.full_stack_v6.clients.repositories.CustomerRepository;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import dev.aj.full_stack_v6.common.domain.entities.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("No authenticated user found");
        }

        Optional<Customer> existingCustomerOptional = customerRepository.findCustomerByUsername(user.getUsername());

        if (existingCustomerOptional.isPresent()) {
            Customer existingCustomer = existingCustomerOptional.get();
            log.warn("Customer {} already exists, Id: {}", existingCustomer.getUser().getUsername(), existingCustomer.getId());
            return existingCustomer;
        }

        customer.setUser(user);

        if (CollectionUtils.isNotEmpty(customer.getAddresses())) {
            for (Address address : customer.getAddresses()) {
                address.setPerson(customer);
            }
        }
        return customerRepository.save(customer);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostAuthorize("hasRole('ADMIN') or returnObject.user.username == authentication.name")
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer id: %d not found".formatted(id)));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void putCustomer(Long id, Customer customer) {
        customerRepository.findById(id)
                .ifPresentOrElse(existing -> {
                            updateCustomerIdempotent(existing, customer);
                            customerRepository.save(existing);
                        },
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no put operation occurred".formatted(id));
                        });
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void patchCustomer(Long id, Customer customer) {
        customerRepository.findById(id)
                .ifPresentOrElse(existing -> {
                            updateCustomerIdempotent(existing, customer);
                            customerRepository.save(existing);
                        },
                        () -> {
                            throw new EntityNotFoundException("Entity ID %d not found, hence no patch occurred".formatted(id));
                        });
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Customer addAddresses(Long customerId, List<Address> addresses) {
        Customer customer = getCustomerById(customerId);
        if (customer.getAddresses() == null) {
            customer.setAddresses(new ArrayList<>());
        }
        for (Address addr : addresses) {
            addr.setPerson(customer);
            customer.getAddresses().add(addr);
        }
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Customer updateAddress(Long customerId, Long addressId, Address address) {
        Customer customer = getCustomerById(customerId);
        if (customer.getAddresses() == null) {
            throw new EntityNotFoundException("No addresses exist for customer %d".formatted(customerId));
        }
        customer.getAddresses().stream()
                .filter(a -> Objects.equals(a.getId(), addressId))
                .findFirst()
                .ifPresentOrElse(existing -> {
                    // update fields idempotently
                    if (address.getAddressType() != null) existing.setAddressType(address.getAddressType());
                    if (address.getStreet() != null) existing.setStreet(address.getStreet());
                    if (address.getCity() != null) existing.setCity(address.getCity());
                    if (address.getState() != null) existing.setState(address.getState());
                    if (address.getCountry() != null) existing.setCountry(address.getCountry());
                    if (address.getPinCode() != null) existing.setPinCode(address.getPinCode());
                }, () -> {
                    throw new EntityNotFoundException("Address Id: %d not found for Customer %d".formatted(addressId, customerId));
                });
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void deleteAddress(Long customerId, Long addressId) {
        Customer customer = getCustomerById(customerId);
        if (customer.getAddresses() == null) {
            log.warn("Customer {} has no addresses; delete skipped", customerId);
            return;
        }
        customer.getAddresses().removeIf(addr -> Objects.equals(addr.getId(), addressId));
        customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerByUserName(String name) {
        return customerRepository.findCustomerByUsername(name)
                .orElseThrow(() -> new EntityNotFoundException("Customer for Username: %s not found".formatted(name)));
    }

    @Override
    public Customer getCustomerByLastnameLike(String lastnamePattern) {
        return customerRepository.findCustomerByLastNameLike("%".concat(lastnamePattern).concat("%"));
    }

    private void updateCustomerIdempotent(Customer existing, Customer modified) {
        if (modified.getFirstName() != null && !Objects.equals(existing.getFirstName(), modified.getFirstName())) {
            existing.setFirstName(modified.getFirstName());
        }
        if (modified.getLastName() != null && !Objects.equals(existing.getLastName(), modified.getLastName())) {
            existing.setLastName(modified.getLastName());
        }
        if (modified.getPhone() != null && !Objects.equals(existing.getPhone(), modified.getPhone())) {
            existing.setPhone(modified.getPhone());
        }
    }
}
