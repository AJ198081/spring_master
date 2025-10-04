package dev.aj.full_stack_v6.common.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Seller extends Person {

    @Builder.Default
    @OneToMany(mappedBy = "seller", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();
}
