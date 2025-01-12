package dev.aj.sdj_hibernate.domain.services.init;

import dev.aj.sdj_hibernate.aspects.LogExecutionTiming;
import dev.aj.sdj_hibernate.domain.entities.Student;
import dev.aj.sdj_hibernate.domain.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersistOp implements ApplicationRunner {

    private final StudentRepository studentRepository;

    @Override
    @LogExecutionTiming
    public void run(ApplicationArguments args) {
        if (studentRepository.count() > 0) {
            return;
        }
        populateStudentTable();
    }

    @LogExecutionTiming
    public void populateStudentTable() {
        Student amar = Student.builder()
                .name("Amar")
                .enrollmentId("z3892389")
                .build();

        Student savedStudent = studentRepository.save(amar);

        savedStudent.setName("AJ");

        Student updatedStudent = studentRepository.save(savedStudent);

        Optional<Student> retrievedStudent = studentRepository
                .findById(savedStudent.getId());

        retrievedStudent.ifPresent(student -> System.out.println(student.getName()));

        studentRepository.deleteById(2L);

        studentRepository.delete(updatedStudent);
    }
}
