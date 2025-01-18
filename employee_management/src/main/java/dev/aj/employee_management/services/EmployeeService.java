package dev.aj.employee_management.services;

import dev.aj.employee_management.entities.Employee;
import dev.aj.employee_management.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;


    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findAllSortedByLastNameAsc() {
        return employeeRepository.findAllByOrderByLastNameAsc();
    }

    public List<Employee> saveAll(List<Employee> list) {
        return employeeRepository.saveAll(list);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }
}
