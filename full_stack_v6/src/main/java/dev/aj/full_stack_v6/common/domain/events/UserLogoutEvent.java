package dev.aj.full_stack_v6.common.domain.events;

import java.security.Principal;

public record UserLogoutEvent(Principal principal) {
}
