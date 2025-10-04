package dev.aj.full_stack_v6.customer.controllers;

import dev.aj.full_stack_v6.customer.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${ADDRESS_API_PATH}")
public class AddressController {

    private final AddressService addressService;



}
