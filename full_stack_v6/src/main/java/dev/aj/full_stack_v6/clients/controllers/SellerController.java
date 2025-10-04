package dev.aj.full_stack_v6.clients.controllers;

import dev.aj.full_stack_v6.clients.SellerService;
import dev.aj.full_stack_v6.common.domain.entities.Address;
import dev.aj.full_stack_v6.common.domain.entities.Seller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("${SELLER_API_PATH}")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;


    @PostMapping("/")
    public ResponseEntity<Seller> createSeller(@Valid @RequestBody Seller Seller, Principal principal) {
        return ResponseEntity.ok(sellerService.createSeller(Seller, principal));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Seller>> getAllSellers(Principal principal) {
        return ResponseEntity.ok(sellerService.getAllSellers(principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable("id") Long id, Principal principal) {
        return ResponseEntity.ok(sellerService.getSellerById(id, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> putSeller(@PathVariable("id") Long id, @RequestBody Seller Seller, Principal principal) {
        sellerService.putSeller(id, Seller, principal);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> patchSeller(@PathVariable("id") Long id, @RequestBody Seller Seller, Principal principal) {
        sellerService.patchSeller(id, Seller, principal);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<Seller> addAddresses(@PathVariable("id") Long id, @RequestBody List<Address> addresses, Principal principal) {
        return ResponseEntity.ok(sellerService.addAddresses(id, addresses, principal));
    }

    @PutMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Seller> updateAddress(@PathVariable("id") Long id,
                                                @PathVariable("addressId") Long addressId,
                                                @RequestBody Address address,
                                                Principal principal) {
        return ResponseEntity.ok(sellerService.updateAddress(id, addressId, address, principal));
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") Long id,
                                              @PathVariable("addressId") Long addressId,
                                              Principal principal) {
        sellerService.deleteAddress(id, addressId, principal);
        return ResponseEntity.ok().build();
    }

}
