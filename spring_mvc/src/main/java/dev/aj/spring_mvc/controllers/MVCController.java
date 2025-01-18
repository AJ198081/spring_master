package dev.aj.spring_mvc.controllers;

import dev.aj.spring_mvc.util.CustomEmailPattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    @Value("${applicable.authorities: ADMIN,USER}")
    private List<String> roles;

    @InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping(value = {"/", "/index"})
    public String renderIndexPage(Model model) {

        List<String> rolesList = roles.stream().map(String::toUpperCase).toList();

        Person person = Person.builder()
//                .firstName("Ajay")
                .build();

        if (Objects.isNull(model.getAttribute("person"))) {
            model.addAttribute("person", person);
            model.addAttribute("countries", countries);
            model.addAttribute("languages", languages);
            model.addAttribute("operatingSystems", operatingSystems);
            model.addAttribute("roles", rolesList);
        }

        return "index";
    }

    @GetMapping(value = {"/registration"})
    public String processGetToForm(Model model, @RequestParam("name") String name, @RequestParam("email") String email) {

        model.addAttribute("name", name.toUpperCase(Locale.ROOT));
        model.addAttribute("email", email.toUpperCase(Locale.ROOT));
        return "registration";

    }
    /**
     * Processes a POST request for the registration form, validating the provided {@code person} object.
     * If validation errors exist, attributes such as the {@code person} object and pre-populated lists
     * (countries, languages, operating systems) are added using {@code addFlashAttribute()} for use
     * in the redirected request.
     *
     * <p>
     * <b>Difference between {@code addFlashAttribute()} and {@code addAttribute()}:</b>
     * <ul>
     *   <li><b>{@code addFlashAttribute()}:</b> Stores the attribute in a flash map, which is internally
     *   maintained in the user's session and removed once the redirected request is fulfilled.
     *   This allows objects of any type to be stored without serialization into request parameters.</li>
     *   <li><b>{@code addAttribute()}:</b> Constructs request parameters from the attributes,
     *   limiting the object types to primitives or {@code String} since they are passed via query parameters.</li>
     * </ul>
     *
     * @param model              the model to store attributes for rendering views
     * @param person             the validated {@code Person} object from the form input
     * @param bindingResult      contains validation errors if any are present
     * @param redirectAttributes used to pass flash attributes during redirection
     * @return a redirect to the "index" page if errors exist; otherwise, stays on the "registration" page
     */
    @PostMapping(value = {"/submit-registration"})
    public String processPostToForm(Model model,
                                    @Validated @ModelAttribute("person") Person person,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            //TODO (left on 18/01/2025): Pay special attention to the name of the BindingResult attribute
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.person", bindingResult);
            redirectAttributes.addFlashAttribute("person", person);
            redirectAttributes.addFlashAttribute("countries", countries);
            redirectAttributes.addFlashAttribute("languages", languages);
            redirectAttributes.addFlashAttribute("operatingSystems", operatingSystems);
            return "redirect:index";
        }

        model.addAttribute("person", person);
        return "registration";
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static final class Person {
        private String firstName;

        @NotNull(message = "Last name cannot be null")
        @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
        private String lastName;

        @Min(value = 2, message = "Age must be greater than 2")
        private int age;

        @CustomEmailPattern(message = "Email must be valid")
        private String email;

        @NotNull(message = "Password cannot be null")
        @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
        private String password;

        @Builder.Default
        private String authority = "USER";

        private LocalDate dob;
        private String gender;
        private String country;
        private String language;
        private List<String> operatingSystem;
    }

}
