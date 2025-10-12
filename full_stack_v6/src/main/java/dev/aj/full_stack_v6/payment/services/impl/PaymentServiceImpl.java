package dev.aj.full_stack_v6.payment.services.impl;

import dev.aj.full_stack_v6.clients.CustomerService;
import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.entities.PaymentDetails;
import dev.aj.full_stack_v6.common.domain.enums.PaymentStatus;
import dev.aj.full_stack_v6.common.domain.enums.PaymentType;
import dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent;
import dev.aj.full_stack_v6.payment.PaymentService;
import dev.aj.full_stack_v6.payment.repositories.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.moments.DayHasPassed;
import org.springframework.modulith.moments.support.Moments;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;
    private final ApplicationEventPublisher eventPublisher;
    private final Moments moments;

    @Override
    public String processCardPayment(PaymentDetails paymentDetails, Principal principal) {

        Payment payment = Payment.builder()
                .paymentType(PaymentType.CREDIT_CARD)
                .paymentDetails(paymentDetails)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        payment.assignCustomer(customerService.getCustomerByUserName(principal.getName()));

        return paymentRepository.save(payment).getPaymentIdentifier().toString();
    }

    @Override
    @PostAuthorize("hasRole('ADMIN') or returnObject.customer.user.username == authentication.name")
    public Payment getPaymentByPaymentId(UUID paymentId, Principal principal) {

        log.info("{} is requesting to retrieve the payment with id {}", principal.getName(), paymentId);

        return paymentRepository.findPaymentByPaymentIdentifier(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment id: %s not found".formatted(paymentId)));
    }

    @EventListener
    @Override
    public void on(DayHasPassed dayHasPassed) {

        Predicate<Payment> isCreatedBeforeToday = payment -> {
            ZonedDateTime createdDate = payment.getAuditMetaData().getCreatedDate();
            return createdDate.toLocalDate().isBefore(LocalDate.from(moments.now()));
        };

        List<Payment> paymentsByPaymentStatus = paymentRepository.findPaymentsByPaymentStatus(PaymentStatus.PENDING);

        paymentsByPaymentStatus
                .stream()
                .filter(isCreatedBeforeToday)
                .forEach(payment -> {
                    if (Objects.nonNull(payment.getOrder())) {
                        payment.setPaymentStatus(PaymentStatus.COMPLETED);
                        paymentRepository.save(payment);
                        eventPublisher.publishEvent(new PaymentSuccessfulEvent(payment.getPaymentIdentifier(), payment.getOrder().getOrderId()));
                    }
                });

    }
}
