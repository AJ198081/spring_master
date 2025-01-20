package dev.aj.security_management.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {

    @GetMapping(path = {"/login", "/"})
    public String login() {
        return "login";
    }

    @GetMapping(path = "/log-out")
    public String logout() {
        return "redirect:/security/login?logout";
    }

    @RequestMapping(path = {"/home"}, method = {GET, POST})
    public String home(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities().stream()
                .map(authority -> {
                    String role = authority.toString();
                    return role.startsWith("ROLE_") ? role.substring(5) : role;
                })
                .findFirst().orElse(null));

        return "home";
    }

    @RequestMapping(path = "/access-denied", method = {GET})
    public String accessDenied() {
        return "access-denied";
    }

}
