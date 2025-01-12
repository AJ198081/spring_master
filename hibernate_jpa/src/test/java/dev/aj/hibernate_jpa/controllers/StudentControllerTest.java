package dev.aj.hibernate_jpa.controllers;

import com.github.javafaker.Faker;
import dev.aj.hibernate_jpa.PostgresTestContainerConfiguration;
import dev.aj.hibernate_jpa.TestDataConfig;
import dev.aj.hibernate_jpa.entities.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(value = {PostgresTestContainerConfiguration.class, TestDataConfig.class})
@TestPropertySource(locations = {"/application-test.properties"})
class StudentControllerTest {

    @Autowired
    private RestClient restClient;

    @Autowired
    private Faker faker;


    @Test
    void saveStudent() {
        Student student = getStudentStream().limit(1).findFirst().get();

        Student savedStudent = restClient.post()
                .uri("/student/")
                .body(student)
                .retrieve()
                .body(Student.class);

        assertNotNull(savedStudent);
        assertNotNull(savedStudent.getId());

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