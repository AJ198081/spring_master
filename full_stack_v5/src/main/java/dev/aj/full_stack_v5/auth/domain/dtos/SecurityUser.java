package dev.aj.full_stack_v5.auth.domain.dtos;

import dev.aj.full_stack_v5.auth.domain.entities.Role;
import dev.aj.full_stack_v5.auth.domain.entities.User;
import dev.aj.full_stack_v5.auth.domain.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
public class SecurityUser extends User implements UserDetails {

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonLocked;

    public SecurityUser(User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked) {
        super(user.getId(), user.getUsername(), user.getPassword(), user.getRoles(), Objects.requireNonNullElse(user.getUserType(), UserType.REGULAR), user.getAuditMetaData());
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonLocked = credentialsNonExpired;
    }

    public SecurityUser(User user) {
        super(user.getId(), user.getUsername(), user.getPassword(), user.getRoles(), Objects.requireNonNullElse(user.getUserType(), UserType.REGULAR), user.getAuditMetaData());
        this.enabled = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonLocked = true;
    }

    public SecurityUser(String username) {
        super(null, username, null, null, UserType.REGULAR, null);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return super.getRoles().stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
