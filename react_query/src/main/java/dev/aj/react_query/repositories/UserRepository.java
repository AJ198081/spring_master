package dev.aj.react_query.repositories;

import dev.aj.react_query.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
