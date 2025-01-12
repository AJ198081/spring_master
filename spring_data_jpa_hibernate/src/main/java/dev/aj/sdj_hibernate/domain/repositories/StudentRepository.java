package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findStudentsByEnrollmentId(String enrollmentId);

    List<Student> findFirst2ByEnrollmentIdContaining(String enrollmentId);

    @Query("select s from Student s where s.name = :name")
    List<Student> findStudentByName(String name);

    List<Student> byName(String name);
}
