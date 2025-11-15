package dev.aj.full_stack_v6.common.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Audited
@AuditTable(value = "customer_history")
public class Customer extends Person {

    @OneToOne(cascade = {ALL}, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Cart cart;

    @OneToMany(mappedBy = "customer")
    @Builder.Default
    @JsonIgnore
    @NotAudited
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = {ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    public void addPayment(Payment newPayment) {
        this.payments.add(newPayment);
    }
}
