package dev.aj.elasticsearch.service;

import dev.aj.elasticsearch.domain.Employee;
import dev.aj.elasticsearch.repositories.EmployeeESRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeESRepositories employeeESRepositories;

    public Employee createEmployee(Employee employee) {
        return employeeESRepositories.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeESRepositories.deleteById(id);
    }

    public Employee findEmployeeById(Long id) {
        return employeeESRepositories.findById(id).orElseThrow(() -> new NoSuchElementException("Employee not found"));
    }

    public List<Employee> addEmployees(List<Employee> employeesToAdd) {
        return Streamable.of(employeeESRepositories.saveAll(employeesToAdd)).toList();
    }

}
