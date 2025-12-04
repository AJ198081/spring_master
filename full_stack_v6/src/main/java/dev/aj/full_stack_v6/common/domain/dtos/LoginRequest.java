package dev.aj.full_stack_v6.common.domain.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.NonNull;

@Schema(description = "Wrapper for Username, Password")
public record LoginRequest(
     @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NonNull String username,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED
//                hidden = true
        ) @NonNull String password) { }
