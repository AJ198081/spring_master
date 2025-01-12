package dev.aj.sdj_hibernate.domain.entities;

import dev.aj.sdj_hibernate.domain.entities.auditing.AuditMetaData;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "passport", schema = "sys_org")
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @EqualsAndHashCode.Include
    private String passportNumber;

    @EqualsAndHashCode.Include
    private String countryCode;

    @OneToOne(mappedBy = "passport")
    private Customer customer;

    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    public void addCustomer(Customer customer) {
        this.customer = customer;
        customer.setPassport(this);
    }

    public void removeCustomer(Customer customer) {
        this.customer = null;
        customer.setPassport(null);
    }
}
