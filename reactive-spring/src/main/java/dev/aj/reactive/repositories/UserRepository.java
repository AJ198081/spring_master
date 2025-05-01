package dev.aj.reactive.repositories;


import dev.aj.reactive.domain.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UserRepository extends ReactiveSortingRepository<User, UUID>, ReactiveCrudRepository<User, UUID> {
    Flux<User> findAllBy(Pageable pageable);
}
