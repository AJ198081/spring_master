package dev.aj.full_stack_v6.order.services.impl;

import dev.aj.full_stack_v6.cart.CartService;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import dev.aj.full_stack_v6.common.domain.entities.CartItem;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.entities.OrderItem;
import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.order.OrderService;
import dev.aj.full_stack_v6.order.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public String placeOrder(Principal principal) {

        Cart customerCart = cartService.getCart(principal);

        Order newOrder = Order.builder()
                .customer(customerCart.getCustomer())
                .payment(Payment.builder()
                        .build())
                .orderItems(customerCart.getCartItems()
                        .stream()
                        .map(this::prepareOrderItemFromCartItem)
                        .toList())
                .build();

        return orderRepository.save(newOrder).getOrderId().toString();
    }

    private OrderItem prepareOrderItemFromCartItem(CartItem cartItem) {

        return OrderItem.builder()
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .orderItemTotalPrice(cartItem.getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .build();
    }


}
