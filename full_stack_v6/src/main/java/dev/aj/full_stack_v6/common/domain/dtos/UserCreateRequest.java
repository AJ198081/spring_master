package dev.aj.full_stack_v6.common.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.stream.Stream;

public record UserCreateRequest(String username, String password, String role) implements UserDetails {

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.ofNullable(role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public UserCreateRequest encodePassword(PasswordEncoder passwordEncoder) {
        return new UserCreateRequest(this.username, passwordEncoder.encode(this.password), this.role);
    }

}

