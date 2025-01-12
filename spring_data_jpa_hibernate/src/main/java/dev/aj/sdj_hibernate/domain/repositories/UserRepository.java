package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findUsersByLevelIn(List<Integer> userLevelIds);

    List<User> findUsersByLevelInOrderByLevelDesc(List<Integer> userLevelIds);

    List<User> findUsersByLevelInOrIsActiveFalse(List<Integer> userLevelIds);

    Page<User> findAll(Pageable pageable);

    Integer deleteByLevel(Integer level);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from User u where u.level = :level")
    Integer deleteInBulkByLevelIn(Integer level);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.username = :name where u.id = :id")
    Integer updateUsers(UUID id, String name);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update versioned User u set u.username = :name where u.id = :id")
    Integer updateUsersAndVersion(UUID id, String name);
}
