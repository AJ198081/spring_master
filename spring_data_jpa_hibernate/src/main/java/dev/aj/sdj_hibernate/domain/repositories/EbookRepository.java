package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EbookRepository extends JpaRepository<Ebook, Long> {
}
