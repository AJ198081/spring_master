package dev.aj.hibernate_jpa.entities.mappers;

import dev.aj.hibernate_jpa.entities.Employee;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {

    EmployeeDTO employeeToDto(Employee employee);

    List<EmployeeDTO> employeesToDtos(List<Employee> employees);
}
