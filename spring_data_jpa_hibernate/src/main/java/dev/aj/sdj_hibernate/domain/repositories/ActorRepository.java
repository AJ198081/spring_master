package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {
}
