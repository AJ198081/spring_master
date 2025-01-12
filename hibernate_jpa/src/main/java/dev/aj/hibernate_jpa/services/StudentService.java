package dev.aj.hibernate_jpa.services;

import dev.aj.hibernate_jpa.entities.Student;

import java.util.List;

public interface StudentService {

    Student save(Student student);
    List<Student> saveStudents(List<Student> students);

    List<Student> findAllStudents();
    Student findStudentById(Long id);
    Student findStudentByLastName(String name);

    int deleteAll();
    void deleteById(Long id);

    Student findStudentByEmail(String email);

    Student findStudentByLastNameOrEmailLike(String partialLastName, String partialPhoneNumber);

    Student updateExistingStudent(Student existingStudent);
}
