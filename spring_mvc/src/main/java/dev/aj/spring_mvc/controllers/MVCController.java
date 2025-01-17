package dev.aj.spring_mvc.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

@Controller
@RequestMapping(value = "/student")
@RequiredArgsConstructor
public class MVCController {

    @GetMapping(value = { "/", "/index"})
    public String renderIndexPage(Model model) {
        model.addAttribute("person", new Person());
        return "index";
    }

    @GetMapping(value = {"/registration"})
    public String processGetToForm(Model model, @RequestParam("name") String name, @RequestParam("email") String email, HttpServletRequest request) {

        model.addAttribute("name", name.toUpperCase(Locale.ROOT));
        model.addAttribute("email", email.toUpperCase(Locale.ROOT));
        return "registration";

    }

    @PostMapping(value = {"/submit-registration"})
    public String processPostToForm(Model model, @ModelAttribute("person") Person person, HttpServletRequest request) {

        model.addAttribute("firstName", person.firstName.toUpperCase(Locale.ROOT));
        model.addAttribute("email", person.email.toUpperCase(Locale.ROOT));
        return "registration";

    }

    public record Student(String name, String email){}

    @NoArgsConstructor
    @Getter @Setter
    public static class StudentDTO{
        private String name;
        private String email;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static final class Person {
        private String firstName;
        private String lastName;
        private String email;
        private LocalDate dob;
        private String gender;
        private String password;
    }

}
