package dev.aj.full_stack_v6.clients;

import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Seller;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface SellerService {
    Seller createSeller(@Valid Seller seller, Principal principal);

    List<Seller> getAllSellers(Principal principal);

     Seller getSellerById(Long id, Principal principal);

     Seller putSeller(Long id, Seller seller, Principal principal);

     Seller patchSeller(Long id, Seller seller, Principal principal);

     Seller addAddresses(Long id, List<Address> addresses, Principal principal);

     Seller updateAddress(Long id, Long addressId, Address address, Principal principal);

     void deleteAddress(Long id, Long addressId, Principal principal);

    Optional<Seller> getSellerByUsername(@NotBlank @Size(min = 1, max = 100) String username);
}
