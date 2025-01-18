package dev.aj.employee_management.controllers;

import dev.aj.employee_management.entities.Employee;
import dev.aj.employee_management.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    List<Employee> employees;

    @InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping(path = {"/all", "/list"})
    public String allEmployees(Model model) {
        employees = employeeService.findAllSortedByLastNameAsc();
        model.addAttribute("employees", employees);
        return "list-employees";
    }

    @GetMapping(path = "/add-employee")
    public String addEmployee(Model model) {
        if (model.getAttribute("org.springframework.validation.BindingResult.employee") == null) {
            model.addAttribute("employee", Employee.builder().build());
        }

        return "add-employee";
    }

    @PostMapping(value = {"/submit-registration"})
    public String persistEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.employee", bindingResult);
            redirectAttributes.addFlashAttribute("employee", employee);
            return "redirect:add-employee";
        }

        Employee persistedEmployee = employeeService.save(employee);

        if (employees == null) {
            employees = employeeService.findAllSortedByLastNameAsc();
        }

        employees.add(persistedEmployee);
        model.addAttribute("employees", employees);
        return "redirect:/employee/all";
    }

    @GetMapping(value = "/update-employee")
    public String updateEmployee(@RequestParam("id") Long id,
                                 Model model) {
        Employee existingEmployee = employeeService.findById(id);
        if (existingEmployee == null) {
            return "redirect:/employee/all";
        }
        model.addAttribute("employee", existingEmployee);
        return "update-employee";
    }

    @GetMapping(value = "/delete-employee")
    public String deleteEmployee(@RequestParam("id") Long id, Model model) {
        employeeService.deleteById(id);
        if (employees == null) {
            employees = employeeService.findAllSortedByLastNameAsc();
        }
        employees.removeIf(employee -> employee.getId().equals(id));
        model.addAttribute("employees", employees);
        return "redirect:/employee/all";
    }
}
