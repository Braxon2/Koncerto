package com.dusan.koncerto.dto.request;

import java.time.LocalDateTime;

public record EventRequestDTO(
        String artist,
        String city,
        String address,
        String venue,
        LocalDateTime dateTime,
        String description
) {
}
