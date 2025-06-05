package com.dusan.koncerto.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BuyTicketRequestDTO(

        @NotBlank(message = "Ticket type cannot be empty")
        String ticketType,

        @Min(value = 1, message = "Minimum quantity can be 1")
        @Max(value = 10, message = "Maximum quantity can be 10")
        int quantity
) {
}
