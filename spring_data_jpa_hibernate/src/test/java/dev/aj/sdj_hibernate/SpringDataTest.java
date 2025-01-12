package dev.aj.sdj_hibernate;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.sdj_hibernate.domain.entities.Student;
import dev.aj.sdj_hibernate.domain.repositories.StudentRepository;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@Import(PostgresConfiguration.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Let's you have 'non-static' class methods
class SpringDataTest {

    public static final String ENROLLMENT_ID = "123456789";

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Student john;

    @SneakyThrows
    @BeforeAll
    void beforeAll() {
        Student jane = Student.builder()
                .name("Jane")
                .enrollmentId("987654321")
                .build();


        Student bob = Student.builder()
                .name("Bob")
                .enrollmentId("111111111")
                .build();

        Student alice = Student.builder()
                .name("Alice")
                .enrollmentId("222222222")
                .build();

        Student charlie = Student.builder()
                .name("Charlie")
                .enrollmentId("333333333")
                .build();

        studentRepository.saveAll(List.of(jane, bob, alice, charlie));

        john = Student.builder()
                .name("John")
                .enrollmentId(ENROLLMENT_ID)
                .build();
    }

    @AfterAll
    void afterAll() {
        studentRepository.deleteAll();
    }

    @Test
    void testCrudOperationsOnStudentDatabase() {

        long initialCount = studentRepository.count();

        Student savedStudent = studentRepository.save(john);
        Assertions.assertThat(savedStudent.getId()).isNotNull();
        Assertions.assertThat(studentRepository.count()).isEqualTo(initialCount + 1L);

        savedStudent.setName("Jane");
        Student updatedStudent = studentRepository.save(savedStudent);
        Assertions.assertThat(updatedStudent.getName()).isEqualTo("Jane");
        Assertions.assertThat(studentRepository.count()).isEqualTo(initialCount + 1L);

        List<Student> students = studentRepository.findStudentsByEnrollmentId("123456789");
        Assertions.assertThat(students.size()).isEqualTo(1);

        List<Student> first2ByEnrollmentIdContaining = studentRepository.findFirst2ByEnrollmentIdContaining("2");
        Assertions.assertThat(first2ByEnrollmentIdContaining.size()).isEqualTo(2);

        List<Student> janes = studentRepository.findStudentByName("Jane");
        Assertions.assertThat(janes.size()).isEqualTo(2);

        studentRepository.deleteById(updatedStudent.getId());
        Assertions.assertThat(studentRepository.count()).isEqualTo(initialCount);
    }

    @Test
    void readStudentByName() {
        Student newStudent = Student.builder()
                .name("name_1")
                .enrollmentId("enrollment_1")
                .build();

        studentRepository.save(newStudent);

        List<Student> students = studentRepository.byName("name_1");
        Assertions.assertThat(students.size()).isEqualTo(1);
    }

}
