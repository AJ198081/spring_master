package dev.aj.full_stack_v2.config.security.csrf;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/csrf-token")
@Slf4j
public class CsrfController {

    @GetMapping
    public CsrfToken getCsrfToken(HttpServletRequest request, CsrfToken token) {

        System.out.println("CsrfToken: " + token);

        return token;
    }
}
