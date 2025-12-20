package dev.aj.full_stack_v6.common.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Stream;

@NullMarked
@Schema(description = "User create request", name = "UserCreateRequest", requiredProperties = {"username", "email", "password", "role"})
public record UserCreateRequest(String username, String email, String password, String role) implements UserDetails {

    public static final String ROLE_PREFIX_STRING = "ROLE_";

    /**
     * Normalizes role; defaults to user if null
     */
    public UserCreateRequest(String username, String email, String password, @Nullable String role) {
        this.username = username;
        this.email = email;
        this.password = password;

        if (role == null) {
            this.role = "ROLE_USER";
        } else {
            this.role = role.toUpperCase().startsWith(ROLE_PREFIX_STRING)
                    ? role.toUpperCase()
                    : ROLE_PREFIX_STRING.concat(role.toUpperCase());
        }
    }

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
}

