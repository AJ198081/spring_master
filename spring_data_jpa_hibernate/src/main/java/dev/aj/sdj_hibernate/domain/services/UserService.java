package dev.aj.sdj_hibernate.domain.services;

import java.util.UUID;

public interface UserService {

    //    @Transactional
    Integer deleteUserByLevel(int level);

    Integer updateUsernameById(UUID id, String username);

    Integer updateUsernameAndVersionById(UUID id, String username);

}
