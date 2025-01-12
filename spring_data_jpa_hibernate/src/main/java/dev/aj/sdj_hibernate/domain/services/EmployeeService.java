package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.Employee;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeNameSalary;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeProjection;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeService {
    void persistEmployee(Employee employee);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateSalary(BigDecimal salary);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateSalaryPessimistically(BigDecimal salary);

    List<EmployeeProjection> findEmployeesWithSalaryGreaterThan(BigDecimal salary);

    @Transactional(readOnly = true)
    List<EmployeeNameSalary> findEmployeesNameAndSalary(BigDecimal salary);
}
