package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Passport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassportRepository extends JpaRepository<Passport, UUID> {
}
