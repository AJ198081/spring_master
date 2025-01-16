package dev.aj.hibernate_jpa.controllers;

import com.github.javafaker.Faker;
import dev.aj.hibernate_jpa.PostgresTestContainerConfiguration;
import dev.aj.hibernate_jpa.SecurityUserInit;
import dev.aj.hibernate_jpa.TestDataConfig;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeCreateDTO;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeDTO;
import dev.aj.hibernate_jpa.entities.mappers.EmployeeCreateMapper;
import dev.aj.hibernate_jpa.repositories.impl.EmployeeDaoImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {PostgresTestContainerConfiguration.class, TestDataConfig.class, SecurityUserInit.class})
@TestPropertySource(locations = {"/application-test.properties"}, properties = {
        "spring.docker.compose.enabled=false",
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.show_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace",
        "logging.level.root=off",
//        "logging.level.org.springframework.*=trace"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {

    @Autowired
    private Faker faker;

    @Autowired
    private EmployeeCreateMapper employeeCreateMapper;

    @Autowired
    private EmployeeDaoImpl employeeDao;

    private EmployeeDTO createdEmployee;

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RestClient.Builder restClientBuilder;

    private RestClient restClient;

    @BeforeAll
    void beforeAll() {
        restClient = restClientBuilder.baseUrl(String.format("http://localhost:%d/%s", port, contextPath))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                // PW:password - ADMIN
                .defaultHeader("Authorization", "Basic UFc6cGFzc3dvcmQ=")
                // TL:password - USER
//                .defaultHeader("Authorization", "Basic VEw6cGFzc3dvcmQ=")
                .build();

        EmployeeCreateDTO employeeCreateDTO = getEmployeeStream().limit(1).findFirst().orElseThrow();

        ResponseEntity<EmployeeDTO> createdEmployeeResponse = restClient.post()
                .uri("/employee")
                .body(employeeCreateDTO)
                .retrieve()
                .toEntity(EmployeeDTO.class);

        createdEmployee = createdEmployeeResponse.getBody();
    }

    @AfterAll
    void afterAll() {

    }

    private Stream<EmployeeCreateDTO> getEmployeeStream() {
        return Stream.generate(() -> new EmployeeCreateDTO(faker.name().firstName(), faker.name().lastName(), faker.internet().safeEmailAddress()));
    }

    @Test
    void getAllEmployees() {
        List<EmployeeDTO> employeeList = restClient.get()
                .uri("/employee/all")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        Assertions.assertThat(employeeList).isNotNull();
        Assertions.assertThat(employeeList.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void getEmployeeById() {

        EmployeeDTO retrievedEmployee = restClient.get()
                .uri("/employee/{id}", createdEmployee.getId())
                .retrieve()
                .body(EmployeeDTO.class);

        Assertions.assertThat(retrievedEmployee).isNotNull();
        Assertions.assertThat(retrievedEmployee.getId()).isEqualTo(createdEmployee.getId());
        Assertions.assertThat(retrievedEmployee)
                .usingRecursiveComparison()
                .isEqualTo(createdEmployee);

    }

    @Test
    void createEmployees() {

        List<EmployeeDTO> createdEmployees = restClient.post()
                .uri("/employee/list")
                .body(getEmployeeStream().limit(5).toList())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        Assertions.assertThat(createdEmployees).isNotNull();
        Assertions.assertThat(createdEmployees.size()).isEqualTo(5);

    }

    @Test
    void updateEmployee() {

        EmployeeCreateDTO employeeCreateDTO = employeeCreateMapper.employeeDtoToCreateDto(createdEmployee);
        employeeCreateDTO.setLastName(createdEmployee.getLastName().concat("_updated"));

        EmployeeDTO updatedEmployee = restClient.put()
                .uri("/employee/{id}", createdEmployee.getId())
                .body(employeeCreateDTO)
                .retrieve()
                .body(EmployeeDTO.class);

        Assertions.assertThat(updatedEmployee).isNotNull();

        Assertions.assertThat(updatedEmployee.getId()).isEqualTo(createdEmployee.getId());

        Assertions.assertThat(updatedEmployee.getLastName()).contains("_updated");

        Assertions.assertThat(updatedEmployee)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(employeeCreateDTO);

        Assertions.assertThat(updatedEmployee).usingRecursiveComparison()
                .ignoringFields("lastName")
                .isEqualTo(createdEmployee);
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            statements = {
                    "insert into sc_hibernate.employee (email, first_name, last_name, id, created_date, created_by)" +
                            " values ('abg@gmail.com', 'M', 'P', 1000, '2025-01-16T20:50:56.818762900+11:00', 'AJ')"
            })
    void deleteEmployee() {
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/employee/{id}", 1000).retrieve().toBodilessEntity();

        Assertions.assertThat(deleteResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }
}