package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CatRepository extends JpaRepository<Cat, UUID> {
}
