package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Paperback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperbackRepository extends JpaRepository<Paperback, Long> {
}
