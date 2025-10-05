package dev.aj.full_stack_v6.clients.repositories;

import dev.aj.full_stack_v6.common.domain.entities.Address;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends JpaRepositoryImplementation<Address, Long> {

}