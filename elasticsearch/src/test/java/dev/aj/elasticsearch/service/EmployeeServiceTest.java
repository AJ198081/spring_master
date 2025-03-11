package dev.aj.elasticsearch.service;

import dev.aj.elasticsearch.ESTCContainerConfig;
import dev.aj.elasticsearch.TestConfig;
import dev.aj.elasticsearch.TestData;
import dev.aj.elasticsearch.domain.Employee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {ESTCContainerConfig.class, TestData.class, TestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties", properties = {
        "logging.level.root=off"
})
@Slf4j
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TestData testData;

    @BeforeEach
    void setUp() {
        log.info("Setting up test...");
    }

    @Test
    void testCreateEmployee() {

        Employee employee = testData.getStreamOfEmployees().limit(1).findFirst().orElseThrow();

        Employee savedEmployee = employeeService.createEmployee(employee);

        Assertions.assertNotNull(savedEmployee);
        org.assertj.core.api.Assertions.assertThat(savedEmployee).usingRecursiveComparison()
                .isEqualTo(employee);
    }

    @Test
    void testDeleteEmployee() {
        Employee employee = testData.getStreamOfEmployees().limit(1).findFirst().orElseThrow();

        Employee savedEmployee = employeeService.createEmployee(employee);

        Assertions.assertDoesNotThrow(() -> employeeService.findEmployeeById(savedEmployee.getId()));
        employeeService.deleteEmployee(savedEmployee.getId());
        Assertions.assertThrows(NoSuchElementException.class, () -> employeeService.findEmployeeById(savedEmployee.getId()));
    }

    @Test
    void testBulkEmployeeAdd() {
        List<Employee> employees = testData.getStreamOfEmployees().limit(10).toList();

        List<Employee> savedEmployees = employeeService.addEmployees(employees);

        Assertions.assertNotNull(savedEmployees);
        Assertions.assertEquals(employees.size(), savedEmployees.size());
        org.assertj.core.api.Assertions.assertThat(savedEmployees)
                .usingRecursiveComparison()
                .isEqualTo(employees);
    }
}