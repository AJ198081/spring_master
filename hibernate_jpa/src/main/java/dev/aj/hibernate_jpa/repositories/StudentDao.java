package dev.aj.hibernate_jpa.repositories;

import dev.aj.hibernate_jpa.entities.Student;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentDao {

    @Transactional
    Student save(Student student);

    @Transactional
    List<Student> saveAllStudents(List<Student> students);

    List<Student> findAll();

    Student findById(Long id);

    Student findByLastName(String lastName);

    int deleteAllStudents();

    void deleteById(Long id);


    Student findByEmail(String email);

    Student findByLastNameOrEmailLike(String partialLastName, String partialPhoneNumber);

    Student updateStudent(Student existingStudent);
}
