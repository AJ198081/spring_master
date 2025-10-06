package dev.aj.full_stack_v6.order.controllers;

import dev.aj.full_stack_v6.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("${ORDER_API_PATH}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final Environment environment;


    @PostMapping("/")
    public ResponseEntity<HttpStatus> createOrder(Principal principal) {
        return ResponseEntity
                .created(URI.create(environment.getRequiredProperty("order.api.path").concat(orderService.placeOrder(principal))))
                .build();
    }


}
