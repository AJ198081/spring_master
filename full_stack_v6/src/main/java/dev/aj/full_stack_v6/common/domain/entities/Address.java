package dev.aj.full_stack_v6.common.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.aj.full_stack_v6.common.domain.enums.AddressType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "address")
@EntityListeners(AuditingEntityListener.class)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(columnDefinition = "VARCHAR(20)")
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
    @Builder.Default
    private String country = "Australia";

    @NotBlank
    @Size(min = 4, message = "PIN must be at least 4-characters")
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonIgnore
    private Person person;

    @Version
    @JdbcTypeCode(SqlTypes.INTEGER)
    private Integer version;

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", pinCode='" + pinCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
