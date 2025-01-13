package dev.aj.hibernate_jpa.repositories;

import dev.aj.hibernate_jpa.entities.Employee;

import java.util.List;

public interface EmployeeDao {

    List<Employee> getAll();

    Employee getById(Long id);

    Employee create(Employee newEmployee);

    Employee update(Long id, Employee employeeToUpdate);

    void delete(Long id);

    List<Employee> createMultiple(List<Employee> employeesToBeCreated);
}
