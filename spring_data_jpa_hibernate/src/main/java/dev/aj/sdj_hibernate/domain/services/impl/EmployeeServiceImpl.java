package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.entities.Employee;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeNameSalary;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeProjection;
import dev.aj.sdj_hibernate.domain.repositories.EmployeeRepository;
import dev.aj.sdj_hibernate.domain.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public void persistEmployee(Employee employee) {
        Employee save = employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateSalary(BigDecimal salary) {
        Employee employee = employeeRepository.findById(1L).orElseThrow();

        employee.setSalary(salary);

        try {
            Thread.sleep(new Random().nextLong(3, 10));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        employeeRepository.save(employee);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateSalaryPessimistically(BigDecimal salary) {
        Employee employee = employeeRepository.findOneForUpdate(1L);

        employee.setSalary(salary);

        try {
            Thread.sleep(new Random().nextLong(3, 10));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EmployeeProjection> findEmployeesWithSalaryGreaterThan(BigDecimal salary) {
        return employeeRepository.findFirst3BySalaryGreaterThan(salary);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EmployeeNameSalary> findEmployeesNameAndSalary(BigDecimal salary) {
        return employeeRepository.findFirst3BySalaryGreaterThanOrderBySalaryDesc(salary);
    }


}
