package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentEntityRepository extends JpaRepository<StudentEntity, Long> {
}
