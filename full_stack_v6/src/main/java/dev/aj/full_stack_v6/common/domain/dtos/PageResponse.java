package dev.aj.full_stack_v6.common.domain.dtos;

import java.util.List;
import java.util.Map;

public record PageResponse<T>(
            List<T> content,
            int number,
            int size,
            long totalElements,
            int totalPages,
            int numberOfElements,
            boolean first,
            boolean last,
            boolean empty,
            Map<String, Object> sort,
            Map<String, Object> pageable
    ) {
    }