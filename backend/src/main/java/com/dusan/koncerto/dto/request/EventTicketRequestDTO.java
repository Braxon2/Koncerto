package com.dusan.koncerto.dto.request;

public record EventTicketRequestDTO(
        String ticketType,
        int quantity,
        double price
) {
}
