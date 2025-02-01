package dev.aj.full_stack_v2.repositories;

import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {
    SecurityUser findByUsername(String username);

    void deleteByUsername(@NotNull String username);
}
