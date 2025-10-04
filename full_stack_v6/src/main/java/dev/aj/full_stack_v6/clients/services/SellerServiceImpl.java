package dev.aj.full_stack_v6.clients.services;

import dev.aj.full_stack_v6.clients.SellerService;
import dev.aj.full_stack_v6.clients.repositories.SellerRepository;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Seller;
import dev.aj.full_stack_v6.common.domain.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;


    @Override
    public Seller createSeller(Seller seller, Principal principal) {

        if (Objects.isNull(principal) || !(((UsernamePasswordAuthenticationToken) principal).getPrincipal() instanceof User user)) {
            throw new IllegalStateException("No authenticated user found");
        }

        Optional<Seller> existingSellerOptional = this.getSellerByUsername(user.getUsername());

        if (existingSellerOptional.isPresent()) {
            Seller existingSeller = existingSellerOptional.get();
            log.warn("Seller {} already exists, Id: {}", existingSeller.getUser().getUsername(), existingSeller.getId());
            return existingSeller;
        }

        seller.setUser(user);

        if (CollectionUtils.isNotEmpty(seller.getAddresses())) {
            seller.getAddresses()
                    .forEach(address -> address.setPerson(seller));
        }

        return sellerRepository.save(seller);
    }

    @Override
    public List<Seller> getAllSellers(Principal principal) {
        return List.of();
    }

    @Override
    public Seller getSellerById(Long id, Principal principal) {
        return null;
    }

    @Override
    public void putSeller(Long id, Seller seller, Principal principal) {

    }

    @Override
    public void patchSeller(Long id, Seller seller, Principal principal) {

    }

    @Override
    public Seller addAddresses(Long id, List<Address> addresses, Principal principal) {
        return null;
    }

    @Override
    public Seller updateAddress(Long id, Long addressId, Address address, Principal principal) {
        return null;
    }

    @Override
    public void deleteAddress(Long id, Long addressId, Principal principal) {

    }

    @Override
    public Optional<Seller> getSellerByUsername(String username) {
        return sellerRepository.findSellerByUsername(username);
    }
}
