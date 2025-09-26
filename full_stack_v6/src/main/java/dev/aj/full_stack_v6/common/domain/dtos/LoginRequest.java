package dev.aj.full_stack_v6.common.domain.dtos;

import org.jspecify.annotations.NonNull;

public record LoginRequest(@NonNull String username, @NonNull String password) {
}
