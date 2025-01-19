package dev.aj.security_management.security.service;

import dev.aj.security_management.security.entities.SecurityUser;
import dev.aj.security_management.security.repositories.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityUserService {

    private final SecurityUserRepository securityUserRepository;

    public SecurityUser save(SecurityUser securityUser) {
        return securityUserRepository.save(securityUser);
    }

    public List<SecurityUser> saveAll(List<SecurityUser> list) {
        return securityUserRepository.saveAll(list);
    }

}
