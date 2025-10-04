package dev.aj.full_stack_v6.clients.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Seller;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepositoryImplementation<Seller, Long> {

    @Query("select s from Seller s where s.user.username = ?1")
    Optional<Seller> findSellerByUsername(@NotBlank @Size(min = 1, max = 100) String username);
}