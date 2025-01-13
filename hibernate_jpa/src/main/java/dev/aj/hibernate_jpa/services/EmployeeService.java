package dev.aj.hibernate_jpa.services;

import dev.aj.hibernate_jpa.entities.dtos.EmployeeCreateDTO;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(Long id);

    @Transactional
    EmployeeDTO createEmployee(EmployeeCreateDTO employeeCreateDTO);

    @Transactional
    EmployeeDTO updateEmployee(Long id, EmployeeCreateDTO employeeToUpdate);

    @Transactional
    void deleteEmployee(Long id);

    List<EmployeeDTO> createMultipleEmployees(List<EmployeeCreateDTO> employeeCreateDTOS);
}
