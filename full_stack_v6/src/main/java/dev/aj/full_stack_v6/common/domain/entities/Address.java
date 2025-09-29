package dev.aj.full_stack_v6.common.domain.entities;

import dev.aj.full_stack_v6.common.domain.enums.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "address_type VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5-characters")
    private String street;

    @NotBlank
    @Size(min = 2, message = "City name must be at least 2-characters")
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name must be at least 2-characters")
    private String state;

    @NotBlank
    @Size(min = 2, message = "Country name must be at least 2-characters")
    private String country;

    @NotBlank
    @Size(min = 4, message = "PIN must be at least 4-characters")
    private String pinCode;

    @ManyToOne
    private User user;

}
