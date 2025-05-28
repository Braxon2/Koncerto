package com.dusan.koncerto.dto.request;

public record BuyTicketRequestDTO(
        String ticketType,
        int quantity
) {
}
