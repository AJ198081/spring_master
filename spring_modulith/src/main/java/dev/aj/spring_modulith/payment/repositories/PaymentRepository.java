package dev.aj.spring_modulith.payment.repositories;

import dev.aj.spring_modulith.payment.entities.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(UUID paymentId);
}
