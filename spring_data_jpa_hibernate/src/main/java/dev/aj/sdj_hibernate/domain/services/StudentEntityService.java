package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.StudentEntity;

import java.util.List;

public interface StudentEntityService {
    StudentEntity persistStudent(StudentEntity student);
    void deleteStudent(StudentEntity student);
    StudentEntity findStudentById(Long id);
    Iterable<StudentEntity> findAllStudents();
    void updateStudent(StudentEntity student);
    void deleteAllStudents();

    List<StudentEntity> persistStudents(List<StudentEntity> students);
}
