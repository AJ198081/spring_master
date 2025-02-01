package dev.aj.full_stack_v2.domain.entities.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.aj.full_stack_v2.domain.entities.AuditMetaData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityUser implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "security_user_seq_generator")
    @SequenceGenerator(name = "security_user_seq_generator", sequenceName = "security_user_seq", schema = "public", allocationSize = 50, initialValue = 1000)
    @Column(name = "security_user_id", columnDefinition = "BIGINT")
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, name = "username", columnDefinition = "VARCHAR(40)")
    private String username;

    @Column(columnDefinition = "VARCHAR(68)")
    @JsonIgnore
    @ToString.Exclude
    private String password;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", schema = "public", joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"))
    @MapKeyColumn(name = "username")
    @Column(name = "authority", columnDefinition = "VARCHAR(40)")
    @JsonProperty("authorities")
    private List<String> authorities = new ArrayList<>();

    private String twoFactorSecret;

    @Builder.Default
    private boolean isTwoFactorEnabled = false;

    @Builder.Default
    private String signUpMethod = "direct";

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean enabled = true;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean accountNonExpired = true;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean accountNonLocked = true;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean credentialsNonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }
}
