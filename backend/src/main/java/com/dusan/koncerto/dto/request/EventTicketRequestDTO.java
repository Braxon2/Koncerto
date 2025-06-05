package com.dusan.koncerto.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record EventTicketRequestDTO(
        @NotBlank(message = "Ticket type cannot be empty")
        String ticketType,

        @Min(value = 5, message = "Minimum quantity can be 5")
        @Max(value = 20000, message = "Maximum quantity can be 20000")
        int quantity,

        @Min(value = 1000, message = "Minimum price can be 1000")
        @Max(value = 500000, message = "Maximum price can be 500 000")
        double price
) {
}
