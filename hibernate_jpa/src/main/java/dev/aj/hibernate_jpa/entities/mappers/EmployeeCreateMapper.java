package dev.aj.hibernate_jpa.entities.mappers;

import dev.aj.hibernate_jpa.entities.Employee;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeCreateDTO;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeCreateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditMetaData", ignore = true)
    Employee createDtoToEmployee(EmployeeCreateDTO employeeCreateDTO);

    EmployeeCreateDTO employeeDtoToCreateDto(EmployeeDTO employeeDTO);

    List<Employee> createDtosToEmployees(List<EmployeeCreateDTO> employeeCreateDTOS);
}
