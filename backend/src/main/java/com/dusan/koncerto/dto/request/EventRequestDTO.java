package com.dusan.koncerto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventRequestDTO(
        @NotBlank(message = "Artist name cannot be empty")
        String artist,

        @NotBlank(message = "City cannot be empty")
        String city,

        @NotBlank(message = "Address cannot be empty")
        String address,

        @NotBlank(message = "Venue cannot be empty")
        String venue,

        @NotNull(message = "Date and time cannot be null")
        LocalDateTime dateTime,

        @NotBlank(message = "Description cannot be empty")
        String description
) {
}
