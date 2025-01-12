package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DogRepository extends JpaRepository<Dog, UUID> {
}
