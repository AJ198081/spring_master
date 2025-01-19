package dev.aj.security_management.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {

    @GetMapping(path = {"/login", "/"})
    public String login() {
        return "login";
    }

    @PostMapping(path = {"/home"})
    public String home() {
        return "home";
    }
}
