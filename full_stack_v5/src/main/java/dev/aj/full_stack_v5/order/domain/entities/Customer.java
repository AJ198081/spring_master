package dev.aj.full_stack_v5.order.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.aj.full_stack_v5.auth.domain.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_gen")
    @SequenceGenerator(name = "customers_gen", sequenceName = "customers_seq", allocationSize = 10)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    private String firstName;
    private String lastName;

    @NaturalId
    @Column(unique = true, nullable = false, columnDefinition = "varchar(100)")
    private String email;

    @Column(columnDefinition = "varchar(30)")
    private String phone;

    @Embedded
    @AttributeOverride(name = "addressLine1", column = @Column(name = "billing_address_line1"))
    @AttributeOverride(name = "addressLine2", column = @Column(name = "billing_address_line2"))
    @AttributeOverride(name = "city", column = @Column(name = "billing_address_city"))
    @AttributeOverride(name = "state", column = @Column(name = "billing_address_state"))
    @AttributeOverride(name = "postalCode", column = @Column(name = "billing_address_post_code"))
    @AttributeOverride(name = "country", column = @Column(name = "billing_address_country"))
    private Address billingAddress;

    @Embedded
    @AttributeOverride(name = "addressLine1", column = @Column(name = "shipping_address_line1"))
    @AttributeOverride(name = "addressLine2", column = @Column(name = "shipping_address_line2"))
    @AttributeOverride(name = "city", column = @Column(name = "shipping_address_city"))
    @AttributeOverride(name = "state", column = @Column(name = "shipping_address_state"))
    @AttributeOverride(name = "postalCode", column = @Column(name = "shipping_address_post_code"))
    @AttributeOverride(name = "country", column = @Column(name = "shipping_address_country"))
    private Address shippingAddress;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "customer", orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private Set<Order> orders = new HashSet<>();

    @OneToOne(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    @ToString.Exclude
    private Cart cart;
}
