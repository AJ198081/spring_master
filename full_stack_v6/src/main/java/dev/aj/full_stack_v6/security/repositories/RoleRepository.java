package dev.aj.full_stack_v6.security.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Role;
import dev.aj.full_stack_v6.common.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(UserRole role);

}
