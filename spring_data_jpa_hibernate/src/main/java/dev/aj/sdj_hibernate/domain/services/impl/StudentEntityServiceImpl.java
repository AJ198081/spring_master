package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.entities.StudentEntity;
import dev.aj.sdj_hibernate.domain.repositories.StudentEntityRepository;
import dev.aj.sdj_hibernate.domain.services.StudentEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentEntityServiceImpl implements StudentEntityService {

    private final StudentEntityRepository studentEntityRepository;

    @Override
    public StudentEntity persistStudent(StudentEntity student) {
        return studentEntityRepository.save(student);
    }

    @Override
    public void deleteStudent(StudentEntity student) {
        studentEntityRepository.delete(student);
    }

    @Override
    public StudentEntity findStudentById(Long id) {
        return studentEntityRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<StudentEntity> findAllStudents() {
        return studentEntityRepository.findAll();
    }

    @Override
    public void updateStudent(StudentEntity student) {
        studentEntityRepository.save(student);
    }

    @Override
    public void deleteAllStudents() {
        studentEntityRepository.deleteAll();
    }

    @Override
    public List<StudentEntity> persistStudents(List<StudentEntity> students) {
        return studentEntityRepository.saveAll(students);
    }

}
