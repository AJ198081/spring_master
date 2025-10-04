package dev.aj.full_stack_v6.customer.controllers;

import dev.aj.full_stack_v6.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${CUSTOMER_API_PATH}")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

}
