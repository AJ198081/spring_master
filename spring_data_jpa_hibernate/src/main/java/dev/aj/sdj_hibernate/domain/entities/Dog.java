package dev.aj.sdj_hibernate.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "dog", schema = "sys_org")
public class Dog extends Animal {

    @NotNull
    private String type;

    public Dog() {
        super();
        this.type = "Dob";
    }

    @Builder
    public Dog(UUID id, String name, int age, String color, String type) {
        super(id, name, age, color);
        this.type = type;
    }
}
