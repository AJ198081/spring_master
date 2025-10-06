package dev.aj.full_stack_v6.order.services.impl;

import dev.aj.full_stack_v6.cart.CartService;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import dev.aj.full_stack_v6.common.domain.entities.CartItem;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.entities.OrderItem;
import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent;
import dev.aj.full_stack_v6.common.domain.events.dto.ShippingDetails;
import dev.aj.full_stack_v6.order.OrderService;
import dev.aj.full_stack_v6.order.repositories.OrderRepository;
import dev.aj.full_stack_v6.payment.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public String placeOrder(UUID paymentIdentifier, Principal principal) {

        Cart customerCart = cartService.getCart(principal);
        Customer currentCustomer = customerCart.getCustomer();

        Payment paymentForThisOrder = paymentService.getPaymentByPaymentId(paymentIdentifier, principal);

        if (paymentForThisOrder.getOrder() != null || !paymentForThisOrder.getCustomer().equals(currentCustomer)) {
            throw new IllegalStateException("Payment for this order does not exist or is not for this customer");
        }

        Order newOrder = Order.builder()
                .build();

        newOrder.assignPayment(paymentForThisOrder, customerCart.getTotalPrice());
        newOrder.setOrderItems(customerCart.getCartItems()
                        .stream()
                        .map(this::prepareOrderItemFromCartItem)
                        .toList());
        newOrder.assignOrderToCustomer(currentCustomer);

        String orderId = orderRepository.save(newOrder).getOrderId().toString();

        eventPublisher.publishEvent(
                new OrderPlacedEvent(
                        orderId,
                        currentCustomer.getId(),
                        currentCustomer.getFirstName(),
                        currentCustomer.getLastName(),
                        new ShippingDetails(currentCustomer),
                        newOrder.getTotalPrice()
                )
        );

        cartService.clearCart(customerCart);

        return orderId;
    }

    @Override
    @PostAuthorize("hasRole('ADMIN') or returnObject.customer.user.username == authentication.name")
    public Order getOrderById(UUID orderId, Principal principal) {

        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order ID %s does not exist".formatted(orderId)));
    }

    private OrderItem prepareOrderItemFromCartItem(CartItem cartItem) {

        OrderItem orderItem = OrderItem.builder()
                .quantity(cartItem.getQuantity())
                .orderItemTotalPrice(cartItem.getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .build();

        orderItem.assignProduct(cartItem.getProduct());

        return orderItem;
    }


}
