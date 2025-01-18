package dev.aj.employee_management.repositories;

import dev.aj.employee_management.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByOrderByLastNameAsc();
}
