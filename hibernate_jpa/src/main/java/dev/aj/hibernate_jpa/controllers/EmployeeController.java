package dev.aj.hibernate_jpa.controllers;

import dev.aj.hibernate_jpa.entities.dtos.EmployeeCreateDTO;
import dev.aj.hibernate_jpa.entities.dtos.EmployeeDTO;
import dev.aj.hibernate_jpa.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping(path = "/all", produces = "application/json")
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDTO getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public EmployeeDTO createEmployee(@RequestBody EmployeeCreateDTO employeeCreateDTO) {
        return employeeService.createEmployee(employeeCreateDTO);
    }

    @PostMapping("/list")
    public List<EmployeeDTO> createEmployees(@RequestBody List<EmployeeCreateDTO> employeeCreateDTOS) {
        return employeeService.createMultipleEmployees(employeeCreateDTOS);
    }

    @PutMapping("/{id}")
    public EmployeeDTO updateEmployee(@PathVariable Long id, @RequestBody EmployeeCreateDTO employeeUpdateDTO) {
        return employeeService.updateEmployee(id, employeeUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}
