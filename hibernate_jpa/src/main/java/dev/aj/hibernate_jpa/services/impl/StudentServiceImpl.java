package dev.aj.hibernate_jpa.services.impl;

import dev.aj.hibernate_jpa.entities.Student;
import dev.aj.hibernate_jpa.repositories.StudentDao;
import dev.aj.hibernate_jpa.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    @Override
    public Student save(Student student) {
        return studentDao.save(student);
    }

    @Override
    public List<Student> saveStudents(List<Student> students) {
        return studentDao.saveAllStudents(students);
    }

    @Override
    public List<Student> findAllStudents() {
        return studentDao.findAll();
    }

    @Override
    public Student findStudentById(Long id) {
        Student retrievedStudent = studentDao.findById(id);
        if(retrievedStudent == null) {
            throw new IllegalArgumentException(String.format("Unable to find student with id: %d", id));
        }
        return retrievedStudent;
    }

    @Override
    public Student findStudentByLastName(String lastName) {
        return studentDao.findByLastName(lastName);
    }

    @Override
    public int deleteAll() {
       return studentDao.deleteAllStudents();
    }

    @Override
    public void deleteById(Long id) {
        studentDao.deleteById(id);
    }

    @Override
    public Student findStudentByEmail(String email) {
        return studentDao.findByEmail(email);
    }

    @Override
    public Student findStudentByLastNameOrEmailLike(String partialLastName, String partialPhoneNumber) {
        return studentDao.findByLastNameOrEmailLike(partialLastName, partialPhoneNumber);

    }

    @Override
    public Student updateExistingStudent(Student existingStudent) {
        return studentDao.updateStudent(existingStudent);
    }

    @Override
    public List<Student> saveListOfStudents(List<Student> students) {
        return studentDao.saveAllStudents(students);
    }

}
