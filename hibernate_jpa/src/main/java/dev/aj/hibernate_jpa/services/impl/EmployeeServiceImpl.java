package dev.aj.hibernate_jpa.services.impl;

import dev.aj.hibernate_jpa.entities.Employee;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeCreateDTO;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeDTO;
import dev.aj.hibernate_jpa.entities.mappers.EmployeeCreateMapper;
import dev.aj.hibernate_jpa.entities.mappers.EmployeeMapper;
import dev.aj.hibernate_jpa.repositories.EmployeeDao;
import dev.aj.hibernate_jpa.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao employeeDao;
    private final EmployeeMapper employeeMapper;
    private final EmployeeCreateMapper employeeCreateMapper;

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> allEmployees = employeeDao.getAll();

        return employeeMapper.employeesToDtos(allEmployees);
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {

        return employeeMapper.employeeToDto(employeeDao.getById(id));
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeCreateDTO employeeCreateDTO) {
        return employeeMapper.employeeToDto(employeeDao.create(employeeCreateMapper.createDtoToEmployee(employeeCreateDTO)));
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeCreateDTO employeeToUpdate) {

        return employeeMapper.employeeToDto(employeeDao.update(id, employeeCreateMapper.createDtoToEmployee(employeeToUpdate)));
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeDao.delete(id);
    }

    @Override
    public List<EmployeeDTO> createMultipleEmployees(List<EmployeeCreateDTO> employeeCreateDTOS) {
        List<Employee> employeesToBeCreated = employeeCreateMapper.createDtosToEmployees(employeeCreateDTOS);
        return employeeMapper.employeesToDtos(employeeDao.createMultiple(employeesToBeCreated));
    }
}
