package dev.aj.hibernate_jpa.repositories.impl;

import dev.aj.hibernate_jpa.entities.security.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {
}
