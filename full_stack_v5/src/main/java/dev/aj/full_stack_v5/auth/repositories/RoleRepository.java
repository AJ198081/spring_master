package dev.aj.full_stack_v5.auth.repositories;

import dev.aj.full_stack_v5.auth.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository <Role, Long>{
    Optional<Role> findRoleByName(String name);
}
