package dev.aj.full_stack_v6_kafka.transfers.repositories;

import dev.aj.full_stack_v6_kafka.common.domain.entities.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {
}
