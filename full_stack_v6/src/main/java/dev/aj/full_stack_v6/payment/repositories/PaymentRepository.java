package dev.aj.full_stack_v6.payment.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Payment;
import dev.aj.full_stack_v6.common.domain.enums.PaymentStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends org.springframework.data.repository.CrudRepository<Payment, Long> {

    Optional<Payment> findPaymentByPaymentIdentifier(UUID paymentId);

    List<Payment> findPaymentsByPaymentStatus(PaymentStatus paymentStatus);
}
