package dev.aj.hibernate_jpa.repositories.impl;

import dev.aj.hibernate_jpa.entities.Employee;
import dev.aj.hibernate_jpa.repositories.EmployeeDao;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmployeeDaoImpl implements EmployeeDao {

    private final EntityManager entityManager;

    @Override
    public List<Employee> getAll() {
        return entityManager.createQuery("select e from Employee e", Employee.class)
                .getResultList();
    }

    @Override
    public Employee getById(Long id) {
        return entityManager.find(Employee.class, id);
    }

    @Override
    @Transactional
    public Employee create(Employee newEmployee) {
        entityManager.persist(newEmployee);
        return newEmployee;
    }

    @Override
    public Employee update(Long id, Employee employeeToUpdate) {

        Employee employee = entityManager.find(Employee.class, id);
        if (employee == null) {
            entityManager.persist(employeeToUpdate);
            return employeeToUpdate;
        } else {
            employeeToUpdate.setId(id);
            return entityManager.merge(employeeToUpdate);
        }
    }

    @Override
    public void delete(Long id) {
        entityManager.createQuery("delete from Employee e where e.id=:id");
    }

    @Override
    @Transactional
    public List<Employee> createMultiple(List<Employee> employeesToBeCreated) {
        employeesToBeCreated.forEach(entityManager::persist);
        return employeesToBeCreated;
    }
}
