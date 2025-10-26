package dev.aj.full_stack_v6.order.services.impl;

import dev.aj.full_stack_v6.cart.CartService;
import dev.aj.full_stack_v6.common.domain.dtos.OrderHistory;
import dev.aj.full_stack_v6.common.domain.entities.Cart;
import dev.aj.full_stack_v6.common.domain.entities.CartItem;
import dev.aj.full_stack_v6.common.domain.entities.Customer;
import dev.aj.full_stack_v6.common.domain.entities.Order;
import dev.aj.full_stack_v6.common.domain.entities.OrderItem;
import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.enums.OrderStatus;
import dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent;
import dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent;
import dev.aj.full_stack_v6.common.domain.events.dto.ShippingDetails;
import dev.aj.full_stack_v6.common.domain.mappers.OrderMapper;
import dev.aj.full_stack_v6.order.OrderService;
import dev.aj.full_stack_v6.order.repositories.OrderRepository;
import dev.aj.full_stack_v6.payment.PaymentService;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;
    private final EntityManager entityManager;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public String placeOrder(UUID paymentIdentifier, Principal principal) {

        Cart customerCart = cartService.getCart(principal);
        Customer currentCustomer = customerCart.getCustomer();

        Payment paymentForThisOrder = paymentService.getPaymentByPaymentId(paymentIdentifier, principal);

        if (paymentForThisOrder.getOrder() != null) {
            throw new IllegalStateException("%s have already been used for order id: %s".formatted(paymentForThisOrder, paymentForThisOrder.getOrder().getOrderId()));
        }

        if (!paymentForThisOrder.getCustomer().equals(currentCustomer)) {
            throw new IllegalStateException("%s do not belong to this customer".formatted(paymentIdentifier));
        }


        Order newOrder = Order.builder()
                .orderStatus(OrderStatus.NEW)
                .build();

        newOrder.assignPayment(paymentForThisOrder, customerCart.getTotalPrice());
        newOrder.setOrderItems(customerCart.getCartItems()
                .stream()
                .map(this::prepareOrderItemFromCartItem)
                .toList());
        newOrder.assignOrderToCustomer(currentCustomer);

        String orderId = orderRepository.save(newOrder)
                .getOrderId()
                .toString();

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

        try {
            Field orderIdField = Order.class.getDeclaredField("orderId");
            Annotation[] orderIdMetaData = orderIdField.getAnnotations();
            if (orderIdMetaData.length != 0) {
                Column orderIdColumn = orderIdField.getAnnotation(Column.class);
                if (orderIdColumn != null) {
                    log.info("OrderID field in Database is: {}", orderIdColumn.name());
                }
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order ID %s does not exist".formatted(orderId)));
    }

    @Override
    @PostAuthorize("hasRole('ADMIN')")
    public List<OrderHistory> getOrderHistory(Long id, Principal principal) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.getRevisions(Order.class, id)
                .stream()
                .map(revision -> auditReader.find(Order.class, id, revision))
                .filter(Objects::nonNull)
                .map(orderMapper::orderToOrderHistory)
                .toList();
    }

    @EventListener
    @Override
    public void on(PaymentSuccessfulEvent paymentSuccessfulEvent) {
        orderRepository.findByOrderId(paymentSuccessfulEvent.orderId())
                .ifPresent(order -> {

                    if (!order.getPayment().getPaymentIdentifier().equals(paymentSuccessfulEvent.paymentIdentifier())) {
                        log.error("Payment identifier {} does not match for order id: {}", paymentSuccessfulEvent.paymentIdentifier(), order.getOrderId());
                    }

                    order.setOrderStatus(OrderStatus.COMPLETED);
                    orderRepository.save(order);
                });
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
