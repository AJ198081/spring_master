package dev.aj.security_management.security.repositories;

import dev.aj.security_management.security.entities.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {
}
