package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.Employee;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeNameSalary;
import dev.aj.sdj_hibernate.domain.entities.projections.EmployeeProjection;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Employee e where e.id = :id")
    Employee findOneForUpdate(Long id);

    List<EmployeeProjection> findFirst3BySalaryGreaterThan(BigDecimal salaryIsGreaterThan);

    List<EmployeeNameSalary> findFirst3BySalaryGreaterThanOrderBySalaryDesc(BigDecimal salaryIsGreaterThan);

}
