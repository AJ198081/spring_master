package dev.aj.spring_mvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/controller")
public class MVCController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("time_now", java.time.LocalDateTime.now());
        return "index";
    }

}
