package dev.aj.full_stack_v6.order;

import java.security.Principal;

public interface OrderService {
    String placeOrder(Principal principal);
}
