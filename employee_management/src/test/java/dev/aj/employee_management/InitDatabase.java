package dev.aj.employee_management;

import com.github.javafaker.Faker;
import dev.aj.employee_management.entities.Employee;
import dev.aj.employee_management.services.EmployeeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import java.util.stream.Stream;

@TestComponent
@Import(value = {EmployeeService.class, TestDataConfig.class})
@RequiredArgsConstructor
public class InitDatabase {

    private final EmployeeService employeeService;
    private final Faker faker;

    @PostConstruct
    public void init() {
        employeeService.saveAll(getEmployees().limit(20).toList());
    }

    private Stream<Employee> getEmployees() {
        return Stream.generate(() -> Employee.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .build());
    }

}
