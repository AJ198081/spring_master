package dev.aj.sdj_hibernate.domain.services.impl;

import dev.aj.sdj_hibernate.domain.repositories.UserRepository;
import dev.aj.sdj_hibernate.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Integer deleteUserByLevel(int level) {
//        Integer numberOfDeletedUsers = userRepository.deleteByLevel(level);
        Integer numberOfDeletedUsers = userRepository.deleteInBulkByLevelIn(level);
        log.info("Number of Deleted Users: {}", numberOfDeletedUsers);
        return numberOfDeletedUsers;
    }

    @Override
    public Integer updateUsernameById(UUID id, String username) {
        return userRepository.updateUsers(id, username);
    }

    @Override
    public Integer updateUsernameAndVersionById(UUID id, String username) {
        return userRepository.updateUsersAndVersion(id, username);
    }
}
