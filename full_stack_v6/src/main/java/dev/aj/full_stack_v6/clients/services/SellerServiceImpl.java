package dev.aj.full_stack_v6.clients.services;

import dev.aj.full_stack_v6.clients.SellerService;
import dev.aj.full_stack_v6.clients.repositories.SellerRepository;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Seller;
import dev.aj.full_stack_v6.common.domain.entities.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
    @PostAuthorize("hasRole('ADMIN')")
    public List<Seller> getAllSellers(Principal principal) {

        log.info("{} is requesting to retrieve all sellers", principal.getName());

        return sellerRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostAuthorize("hasRole('ADMIN') or returnObject.user.username == authentication.name")
    public Seller getSellerById(Long id, Principal principal) {

        log.info("{} is requesting to retrieve the seller profile with id {}", principal.getName(), id);

        return sellerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller id: %d not found".formatted(id)));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Seller putSeller(Long id, Seller seller, Principal principal) {
        Seller existingSellerProfile = validateCanUpdateAndGetExistingSeller(id, principal);

        existingSellerProfile.setPhone(seller.getPhone());
        existingSellerProfile.setFirstName(seller.getFirstName());
        existingSellerProfile.setLastName(seller.getLastName());
        existingSellerProfile.getAddresses().clear();

        seller.getAddresses()
                .forEach(address -> address.setPerson(existingSellerProfile));

        existingSellerProfile.getAddresses().addAll(seller.getAddresses());

        return sellerRepository.save(existingSellerProfile);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Seller patchSeller(Long id, Seller seller, Principal principal) {

        Seller existingSellerProfile = validateCanUpdateAndGetExistingSeller(id, principal);

        if (Objects.nonNull(seller.getPhone())) {
            existingSellerProfile.setPhone(seller.getPhone());
        }

        if (Objects.nonNull(seller.getFirstName())) {
            existingSellerProfile.setFirstName(seller.getFirstName());
        }

        if (Objects.nonNull(seller.getLastName())) {
            existingSellerProfile.setLastName(seller.getLastName());
        }

        if (CollectionUtils.isNotEmpty(seller.getAddresses())) {
            existingSellerProfile.getAddresses().clear();
            seller.getAddresses()
                    .forEach(address -> address.setPerson(existingSellerProfile));
            existingSellerProfile.getAddresses().addAll(seller.getAddresses());
        }

        return sellerRepository.save(existingSellerProfile);
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

    @SuppressWarnings("SpringSecurityMethodCallsInspection")
    private Seller validateCanUpdateAndGetExistingSeller(Long id, Principal principal) {
        Seller existingSellerProfile = this.getSellerById(id, principal);

        if (((UsernamePasswordAuthenticationToken) principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && !existingSellerProfile.getUser().getUsername().equals(principal.getName())) {
            throw new AuthorizationDeniedException("User %s is not authorized to update profile of user %s".formatted(principal.getName(), existingSellerProfile.getUser().getUsername()));
        }
        return existingSellerProfile;
    }
}
