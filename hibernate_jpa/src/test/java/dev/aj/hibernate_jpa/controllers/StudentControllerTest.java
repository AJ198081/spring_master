package dev.aj.hibernate_jpa.controllers;

import com.github.javafaker.Faker;
import dev.aj.hibernate_jpa.PostgresTestContainerConfiguration;
import dev.aj.hibernate_jpa.TestDataConfig;
import dev.aj.hibernate_jpa.entities.Student;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(value = {PostgresTestContainerConfiguration.class, TestDataConfig.class})
@TestPropertySource(locations = {"/application-test.properties"}, properties = {
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.show_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerTest {

    @Autowired
    private RestClient restClient;

    @Autowired
    private Faker faker;

    private List<Student> initiallySavedStudents;

    @BeforeAll
    void beforeAll() {
        List<Student> initialStudents = getStudentStream().limit(5).toList();
        initiallySavedStudents = restClient.post()
                .uri("/student/list")
                .body(initialStudents)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Student>>() {
                })
                .getBody();
    }

    @Test
    void testGetStudentById() {
        Student firstStudent = initiallySavedStudents.getFirst();

        Student retrievedStudent = restClient.get()
                .uri("/student/{studentId}", firstStudent.getId())
                .retrieve()
                .body(Student.class);

        Assertions.assertThat(retrievedStudent).usingRecursiveComparison().isEqualTo(firstStudent);
    }

    @Test
    void testGetNonExistentStudentById() {
        Student firstStudent = initiallySavedStudents.getFirst();

        List<Student> allStudents = restClient.get()
                .uri("/student/all")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        assert allStudents != null;

        long highestStudentId = allStudents.stream().mapToLong(Student::getId).max().orElseThrow();

        RestClient.ResponseSpec responseSpec = restClient.get()
                .uri("/student/{studentId}", highestStudentId + 1)
                .retrieve();

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () -> responseSpec.body(Student.class));
    }

    @Test
    void testGetStudentForInvalidId() {

        RestClient.ResponseSpec responseSpec = restClient.get()
                .uri("/student/{studentId}", OptionalLong.empty())
                .retrieve();

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () -> responseSpec.body(Student.class));
    }

    @Test
    void testPersistSingleStudent() {
        Student student = getStudentStream().limit(1).findFirst().orElseThrow();

        RestClient.ResponseSpec responseSpec = restClient.post()
                .uri("/student")
                .body(student)
                .retrieve();

        Student savedStudent = responseSpec
                .body(Student.class);

        assertThat(savedStudent).isNotNull();

        assertThat(savedStudent.getId()).isNotNull();

        Assertions.assertThat(savedStudent)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);

    }

    @Test
    void testPersistListOfStudents() {

        List<Student> listOfStudents = getStudentStream().limit(5).toList();
        ResponseEntity<List<Student>> responseEntity = restClient.post()
                .uri("/student/list")
                .body(listOfStudents)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody().size()).isEqualTo(5);
        Assertions.assertThat(responseEntity.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(listOfStudents);


    }

    @Test
    void updateStudent() {
        Student lastStudent = initiallySavedStudents.getLast();
        lastStudent.setLastName(lastStudent.getLastName().concat("_updated"));

        Student updatedStudent = restClient.put()
                .uri("/student")
                .body(lastStudent)
                .retrieve()
                .body(Student.class);

        Student updatedStudentRetrievedAgain = restClient.get()
                .uri("/student/{studentId}", lastStudent.getId())
                .retrieve()
                .body(Student.class);

        assertThat(updatedStudentRetrievedAgain)
                .usingRecursiveComparison()
                .isEqualTo(updatedStudent);

        assert updatedStudent != null;
        assertThat(updatedStudent.getLastName()).contains("_updated");

        assertThat(updatedStudent)
                .usingRecursiveComparison()
                .isEqualTo(lastStudent);

    }

    Stream<Student> getStudentStream() {
        return Stream.generate(() -> Student.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .build());
    }
}