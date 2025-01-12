package dev.aj.hibernate_jpa.services;

import com.github.javafaker.Faker;
import dev.aj.hibernate_jpa.PostgresTestContainerConfiguration;
import dev.aj.hibernate_jpa.TestDataConfig;
import dev.aj.hibernate_jpa.entities.Student;
import dev.aj.hibernate_jpa.repositories.impl.StudentDaoImpl;
import dev.aj.hibernate_jpa.services.impl.StudentServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@Import({StudentServiceImpl.class, TestDataConfig.class, PostgresTestContainerConfiguration.class, StudentDaoImpl.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = {"/application-test.properties"}, properties = {
        "logging.level.root=off",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.properties.hibernate.show_sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace"
})
class StudentServiceTest {

    @Autowired
    private StudentServiceImpl studentService;

    @Autowired
    private Faker faker;

    @BeforeAll
    void beforeAll() {
        studentService.saveStudents(getStreamOfStudents()
                .limit(5)
                .toList()
        );
    }

    @AfterAll
    void afterAll() {
        int deletedCount = studentService.deleteAll();
        assertThat(deletedCount, greaterThanOrEqualTo(5));
        System.out.printf("Deleted %d students%n", deletedCount);
    }

    @Test
    void save() {
        Student student = Student.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build();

        Student persistedStudent = studentService.save(student);

        assertThat(persistedStudent, is(notNullValue()));
        assertThat(persistedStudent.getId(), notNullValue());
    }

    @Test
    void saveStudents() {
        List<Student> listOf10Students = getStreamOfStudents().limit(5).toList();
        List<Student> savedStudents = studentService.saveStudents(listOf10Students);
        assertThat(savedStudents.size(), is(5));
        savedStudents.forEach(student -> assertThat(student.getId(), notNullValue()));
    }


    @Test
    void findAllStudents() {
        List<Student> allStudents = studentService.findAllStudents();
        Assertions.assertThat(allStudents.size()).isGreaterThan(4);
    }

    @Test
    void findStudentById() {
        List<Student> allStudents = studentService.findAllStudents();
        Student student = studentService.findStudentById(allStudents.getFirst().getId());

        Assertions.assertThat(student).isNotNull();
        Assertions.assertThat(student.getId()).isEqualTo(allStudents.getFirst().getId());
    }

    @Test
    void findStudentByLastName() {
        List<Student> allStudents = studentService.findAllStudents();
        Student student = studentService.findStudentByLastName(allStudents.getFirst().getLastName());
        Assertions.assertThat(student).isNotNull();
        Assertions.assertThat(student.getLastName()).isEqualTo(allStudents.getFirst().getLastName());
    }

    @Test
    void findStudentByEmail() {
        List<Student> allStudents = studentService.findAllStudents();
        Student student = studentService.findStudentByEmail(allStudents.getFirst().getEmail());
    }

    @Test
    void findStudentByLikeLastNameOrLikeEmail() {
        List<Student> allStudents = studentService.findAllStudents();
        Student selectedStudent = allStudents.getFirst();
        String lastNameOfSelectedStudent = selectedStudent.getLastName();

        Student student = studentService.findStudentByLastNameOrEmailLike(lastNameOfSelectedStudent.substring(0, lastNameOfSelectedStudent.length() - 1),
                selectedStudent.getEmail().substring(0, 5)
        );
    }

    @Test
    void updateStudent() {
        List<Student> allStudents = studentService.findAllStudents();

        Student lastStudent = allStudents.getLast();

        Student updatedStudent = updateStudent(lastStudent);

        Student mergedStudent = studentService.updateExistingStudent(updatedStudent);

        Assertions.assertThat(mergedStudent).isNotNull();
        Assertions.assertThat(mergedStudent.getId()).isEqualTo(lastStudent.getId());
        assertThat(mergedStudent.getLastName(), containsString("_updated"));
    }

    private Student updateStudent(Student existingStudent) {
        return Student.builder()
                .id(existingStudent.getId())
                .firstName(existingStudent.getFirstName())
                .lastName(existingStudent.getLastName().concat("_updated"))
                .email(existingStudent.getEmail())
                .phone(existingStudent.getPhone())
                .build();
    }

    private Stream<Student> getStreamOfStudents() {
        return Stream.generate(() -> Student.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build());
    }
}