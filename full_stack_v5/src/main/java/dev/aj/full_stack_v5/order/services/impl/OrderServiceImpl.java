package dev.aj.full_stack_v5.order.services.impl;

import dev.aj.full_stack_v5.order.domain.entities.Cart;
import dev.aj.full_stack_v5.order.domain.entities.Order;
import dev.aj.full_stack_v5.order.domain.entities.OrderItem;
import dev.aj.full_stack_v5.order.domain.entities.enums.OrderStatus;
import dev.aj.full_stack_v5.order.repositories.OrderRepository;
import dev.aj.full_stack_v5.order.services.CartService;
import dev.aj.full_stack_v5.order.services.OrderService;
import dev.aj.full_stack_v5.product.domain.entities.Product;
import dev.aj.full_stack_v5.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CartService cartService;

    @Override
    public Order createOrder(Long customerId) {

        Optional<Cart> customerCart = cartService.getCartByCustomerId(customerId);

        if (customerCart.isEmpty()) {
            log.error("Unable to create order for customerId: {} as cart not found", customerId);
            throw new IllegalArgumentException("Unable to create an order for customerId: %s as no cart found".formatted(customerId));
        }

        Cart cart = customerCart.get();

        Order newOrder = createOrder(cart);

        Set<OrderItem> orderItems = createOrderItems(newOrder, cart);

        newOrder.setOrderItems(orderItems);
        newOrder.updateTotal();

        return orderRepository.save(newOrder);
    }

    @Override
    public List<Order> getCustomerOrders(Long customerId) {

        return orderRepository.findOrderByCustomerId(customerId);
    }

    private BigDecimal calculateOrderTotalAmount(List<OrderItem> orderItemList) {

        return orderItemList.stream()
                .map(OrderItem::getOrderItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderItem> createOrderItems(Order order, Cart cart) {

        return cart.getCartItems()
                .stream()
                .map(cartItem -> {
                    Product cartProduct = cartItem.getProduct();
                    cartProduct.setInventory(cartProduct.getInventory() - cartItem.getQuantity());
                    productService.updateProduct(cartProduct);
                    return OrderItem.builder()
                            .order(order)
                            .product(cartProduct)
                            .quantity(cartItem.getQuantity())
                            .price(cartProduct.getPrice())
                            .build();
                })
                .collect(Collectors.toSet());
    }

    private Order createOrder(Cart cart) {
        return Order.builder()
                .status(OrderStatus.PENDING)
                .orderDate(ZonedDateTime.now())
                .customer(cart.getCustomer())
                .build();
    }

}
