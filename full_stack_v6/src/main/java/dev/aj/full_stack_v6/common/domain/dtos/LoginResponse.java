package dev.aj.full_stack_v6.common.domain.dtos;

import java.util.List;

public record LoginResponse(String jwt, String username, List<String> roles) {
}
