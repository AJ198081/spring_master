package dev.aj.full_stack_v5.order.domain.entities;

import dev.aj.full_stack_v5.order.domain.entities.enums.STATE;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Address {

    private String addressLine1;
    private String addressLine2;
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "character varying(35) default 'NSW'")
    private STATE state;

    private String postalCode;

    @Builder.Default
    private String country = "Australia";

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s, %s", addressLine1, addressLine2, city, state, postalCode, country);
    }

}
