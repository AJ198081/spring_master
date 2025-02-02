package dev.aj.full_stack_v2.domain.dto;

import java.util.List;

public record LoginResponse(String jwtToken, String username, List<String> roles) {
}
