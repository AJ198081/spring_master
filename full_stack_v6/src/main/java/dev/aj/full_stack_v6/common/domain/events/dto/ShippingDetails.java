package dev.aj.full_stack_v6.common.domain.events.dto;

import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import dev.aj.full_stack_v6.common.domain.enums.AddressType;
import jakarta.persistence.EntityNotFoundException;

@SuppressWarnings("unused")
public record ShippingDetails(Address shippingAddress, String email) {

    @SuppressWarnings("unused")
    public ShippingDetails(Customer customer) {
        this(
                customer.getAddresses().stream()
                        .filter(ShippingDetails::isShippingAddress)
                        .findAny()
                        .orElseThrow(() -> new EntityNotFoundException("Shipping address missing for user %s".formatted(customer.getUser().getUsername()))),
                customer.getUser().getEmail()
        );
    }

    private static boolean isShippingAddress(Address addressToCheck) {
        return addressToCheck.getAddressType().equals(AddressType.SHIPPING);
    }
}
