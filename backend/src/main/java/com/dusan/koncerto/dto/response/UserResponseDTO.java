package com.dusan.koncerto.dto.response;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
