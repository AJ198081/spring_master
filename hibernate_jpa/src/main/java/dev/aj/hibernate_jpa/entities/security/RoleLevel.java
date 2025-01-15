package dev.aj.hibernate_jpa.entities.security;

import lombok.Getter;

@Getter
public enum RoleLevel {

    ADMIN( "ADMIN"),
    USER( "USER"),
    SUPPORT("SUPPORT"),
    DEVELOPER( "DEVELOPER"),
    TESTER( "TESTER"),
    GUEST( "GUEST");

    private final String roleLevel;

    RoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    @Override
    public String toString() {
        return String.format("ROLE_%s", roleLevel);
    }
}
