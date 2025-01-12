package dev.aj.sdj_hibernate.domain.services;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.Employee;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeNameSalary;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeProjection;
import dev.aj.sdj_hibernate.domain.repositories.EmployeeRepository;
import dev.aj.sdj_hibernate.domain.services.impl.EmployeeServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@DataJpaTest
@Import({PostgresConfiguration.class, EmployeeServiceImpl.class, Faker.class, AuditingConfig.class})
@TestPropertySource(locations = {"/application-test.properties", "/junit-platform.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "logging.level.org.springframework.orm.ObjectOptimisticLockingFailureException = trace",
        "logging.level.org.hibernate.orm.core.OptimisticLockException = trace",
        "logging.level.jakarta.persistence.OptimisticLockException=trace",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace",
     /*   "spring.datasource.hikari.auto-commit=true",
        "spring.jpa.properties.hibernate.connection.provider_disables_autocommit=false"*/
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private Faker faker;

    private ExecutorService executorService;

    @BeforeAll
    void setUp() {

        getEmployeeDetailsStream().limit(1)
                .forEach(employeeService::persistEmployee);

        executorService = Executors.newFixedThreadPool(100);
    }

    @AfterAll
    void tearDown() {
        employeeRepository.deleteAll();

        executorService.shutdown();
    }

    @RepeatedTest(5)
    void testConcurrentUpdates_1() {
        for (int i = 0; i < 100; i++) {
            BigDecimal updatedSalary = new BigDecimal(new Random().nextInt(10, 100));
            Runnable runnable = () -> employeeService.updateSalary(updatedSalary);
            executorService.execute(runnable);
        }
        testForConcurrentUpdates();
    }


    @RepeatedTest(5)
    void testFixConcurrentUpdates_1() {
        for (int i = 0; i < 100; i++) {
            BigDecimal updatedSalary = new BigDecimal(new Random().nextInt(10, 100));
            Runnable runnable = () -> employeeService.updateSalaryPessimistically(updatedSalary);
            executorService.execute(runnable);
        }
        testFixForConcurrentUpdates();
    }

    @Test
    void testEmployeeProjection() {

        getEmployeeDetailsStream().limit(50)
                .peek(employee -> employee.setSalary(new BigDecimal(new Random().nextInt(10, 100))))
                .forEach(employeeService::persistEmployee);

        List<EmployeeProjection> employeeProjectionsAboveThreshold = employeeService.findEmployeesWithSalaryGreaterThan(BigDecimal.valueOf(50));

        Assertions.assertThat(employeeProjectionsAboveThreshold).hasSize(3)
                .allSatisfy(employee -> Assertions.assertThat(employee.salary()).isGreaterThan(BigDecimal.valueOf(50.0)));
    }

    @Test
    void testEmployeeInterfaceProjection() {

        getEmployeeDetailsStream().limit(50)
                .peek(employee -> employee.setSalary(new BigDecimal(new Random().nextInt(10, 100))))
                .forEach(employeeService::persistEmployee);

        List<EmployeeNameSalary> employeeProjectionsProxy = employeeService.findEmployeesNameAndSalary(BigDecimal.valueOf(50));

        Assertions.assertThat(employeeProjectionsProxy).hasSize(3)
                .allSatisfy(employee -> Assertions.assertThat(employee.getSalary()).isGreaterThan(BigDecimal.valueOf(50.0)))
//                .extracting(EmployeeNameSalary::getFormattedSalary)
//                .contains("\t")
        ;
    }

    private void testForConcurrentUpdates() {

        Runnable firstClient = () -> employeeService.updateSalaryPessimistically(BigDecimal.ONE);
        Runnable secondClient = () -> employeeService.updateSalaryPessimistically(BigDecimal.TWO);
        try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {
            executorService.submit(secondClient);
            executorService.submit(firstClient);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThat(employeeRepository.findById(1L).orElseThrow()
                        .getSalary())
                .isNotNull();
    }

    private void testFixForConcurrentUpdates() {
        Runnable firstClient = () -> employeeService.updateSalaryPessimistically(BigDecimal.ONE);
        Runnable secondClient = () -> employeeService.updateSalaryPessimistically(BigDecimal.TWO);
        try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {
            executorService.submit(secondClient);
            executorService.submit(firstClient);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThat(employeeRepository.findById(1L).orElseThrow()
                        .getSalary())
                .isNotNull();
    }


    private Stream<Employee> getEmployeeDetailsStream() {
        return Stream.generate(() -> Employee.builder()
                .name(faker.name().fullName())
                .staff_id(faker.bothify("SC-###???", true))
                .build());
    }
}