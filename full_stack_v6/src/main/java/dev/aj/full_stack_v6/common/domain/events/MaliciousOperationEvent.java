package dev.aj.full_stack_v6.common.domain.events;

import java.time.Instant;

public record MaliciousOperationEvent(String name, String email, String message, Instant timestamp) {
}
