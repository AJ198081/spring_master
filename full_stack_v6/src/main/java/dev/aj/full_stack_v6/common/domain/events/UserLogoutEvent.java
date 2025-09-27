package dev.aj.full_stack_v6.common.domain.events;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.aj.full_stack_v6.common.domain.json.PrincipalDeserializer;

import java.security.Principal;
import java.time.Instant;

public record UserLogoutEvent(@JsonDeserialize(using = PrincipalDeserializer.class) Principal principal, String message, Instant timestamp) {
}


