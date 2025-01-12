package dev.aj.sdj_hibernate.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "cat", schema = "sys_org")
public class Cat extends Animal {

    @NotNull
    private String type;

    @Builder
    public Cat(UUID id, String name, int age, String color, String type) {
        super(id, name, age, color);
        this.type = type;
    }

    public Cat() {
        super();
    }
}
