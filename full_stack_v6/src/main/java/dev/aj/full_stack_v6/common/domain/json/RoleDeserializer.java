package dev.aj.full_stack_v6.common.domain.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.aj.full_stack_v6.common.domain.entities.Role;
import dev.aj.full_stack_v6.common.domain.enums.UserRole;

import java.io.IOException;

public class RoleDeserializer extends JsonDeserializer<Role> {
    @Override
    public Role deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonNode node = p.getCodec().readTree(p);

        String role = node.get("role").asText();

        if (role != null) {
            return Role.builder()
                    .role(UserRole.valueOf(role.trim().toUpperCase()))
                    .build();
        }

        return null;
    }
}
