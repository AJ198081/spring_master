package dev.aj.spring_mvc.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "/student")
@RequiredArgsConstructor
public class MVCController {

    @Value("${available.countries: Ban,SL,SA,Eng}")
    private List<String> countries;

    @Value("${available.languages: Java,React,Javascript }")
    private List<String> languages;

    @Value("${operating.systems: Windows,OS,Linux}")
    private List<String> operatingSystems;

    @GetMapping(value = {"/", "/index"})
    public String renderIndexPage(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("countries", countries);
        model.addAttribute("languages", languages);
        model.addAttribute("operatingSystems", operatingSystems);
        return "index";
    }

    @GetMapping(value = {"/registration"})
    public String processGetToForm(Model model, @RequestParam("name") String name, @RequestParam("email") String email) {

        model.addAttribute("name", name.toUpperCase(Locale.ROOT));
        model.addAttribute("email", email.toUpperCase(Locale.ROOT));
        return "registration";

    }

    @PostMapping(value = {"/submit-registration"})
    public String processPostToForm(Model model, @ModelAttribute("person") Person person) {

        model.addAttribute("person", person);
        return "registration";
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static final class Person {
        private String firstName;
        private String lastName;
        private String email;
        private LocalDate dob;
        private String gender;
        private String country;
        private String language;
        private List<String> operatingSystem;
        private String password;
    }

}
