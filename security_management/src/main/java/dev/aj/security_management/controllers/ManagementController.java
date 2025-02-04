package dev.aj.security_management.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management")
public class ManagementController {

    @GetMapping("/leader")
    public String home() {
        return "leaders";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admins";
    }


}
