package dev.aj.spring_modulith;

import com.github.javafaker.Faker;
import dev.aj.spring_modulith.inventory.entities.Inventory;
import dev.aj.spring_modulith.order.entities.Order;
import dev.aj.spring_modulith.order.entities.types.OrderStatus;
import dev.aj.spring_modulith.payment.entities.Payment;
import dev.aj.spring_modulith.payment.entities.types.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
@Import(value = {Faker.class, TestConfig.class})
public class TestData {

    private final Faker faker;

    public Stream<Order> getStreamOfOrder() {

        OrderStatus[] orderStatuses = OrderStatus.values();

        return Stream.generate(() -> Order.builder()
                .customerName(faker.name().fullName())
                .customerEmail(faker.internet().emailAddress())
                .orderStatus(orderStatuses[faker.random().nextInt(orderStatuses.length)])
                .build());
    }

    public Stream<Inventory> getStreamOfInventory() {
        return Stream.generate(() -> Inventory.builder()
                .description(faker.lorem().sentence())
                .quantity(faker.number().numberBetween(1, 100))
                .name(faker.commerce().productName())
                .price(new BigDecimal(faker.commerce().price(95.00, 500.00)))
                .build());
    }

    public Stream<Payment> getStreamOfPayment() {

        PaymentStatus[] paymentStatuses = PaymentStatus.values();

        return Stream.generate(() -> Payment.builder()
                .amount(new BigDecimal(faker.commerce().price(95.00, 500.00)))
                .paymentStatus(paymentStatuses[faker.random().nextInt(paymentStatuses.length)])
                .build());

    }
}
