package dev.aj.full_stack_v5.auth.repositories;

import dev.aj.full_stack_v5.auth.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findUserByUsername(String username);
}
