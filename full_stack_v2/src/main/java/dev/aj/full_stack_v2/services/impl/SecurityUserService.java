package dev.aj.full_stack_v2.services.impl;

import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import dev.aj.full_stack_v2.repositories.SecurityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsManager {

    private final SecurityUserRepository securityUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void createUser(UserDetails user) {
       if (user instanceof SecurityUser){
           securityUserRepository.save((SecurityUser) user);
       } else {
           throw new IllegalArgumentException("Only SecurityUser is supported");
       }
    }

    @Transactional
    @Override
    public void updateUser(UserDetails user) {
        securityUserRepository.save((SecurityUser) user);
    }

    @Transactional
    @Override
    public void deleteUser(String username) {
        securityUserRepository.deleteByUsername(username);
    }

    @Transactional
    @Override
    public void changePassword(String userName, String newPassword) {
        if (userExists(userName)) {
            SecurityUser user = securityUserRepository.findByUsername(userName);
            user.setPassword(passwordEncoder.encode(newPassword));
            securityUserRepository.save(user);
        }
    }

    @Override
    public boolean userExists(String username) {
        return Objects.nonNull(loadUserByUsername(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return securityUserRepository.findByUsername(username);
    }

    public List<SecurityUser> getAllUsersInDatabase() {
        return securityUserRepository.findAll();
    }

    public SecurityUser saveUser(SecurityUser newUser) {
        return securityUserRepository.save(newUser);
    }

    @Transactional
    public SecurityUser updateUserByUsername(String username, SecurityUser userToBeUpdated) {
        if (!userExists(username)) {
            throw new IllegalArgumentException("User with username '%s' does not exist.".formatted(username));
        }

        SecurityUser existingUser = (SecurityUser) loadUserByUsername(username);

        if (userToBeUpdated.getUsername() != null) {
            existingUser.setUsername(userToBeUpdated.getUsername());
        }

        if (userToBeUpdated.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userToBeUpdated.getPassword())); // Always encode passwords
        }

        if (userToBeUpdated.getAuthorities() != null) {
            existingUser.setAuthorities(userToBeUpdated.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        }

        return securityUserRepository.save(existingUser);
    }

    public String updateUserRole(String username, String role) {

        if (!userExists(username)) {
            throw new IllegalArgumentException("User with username '%s' does not exist.".formatted(username));
        }

        SecurityUser existingUser = securityUserRepository.findByUsername(username);
        List<String> roles = new ArrayList<>();
        roles.add(role);
        existingUser.setAuthorities(roles);
        securityUserRepository.save(existingUser);

        return role;

    }
}
