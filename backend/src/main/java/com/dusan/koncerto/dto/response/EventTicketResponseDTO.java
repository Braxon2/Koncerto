package com.dusan.koncerto.dto.response;


public record EventTicketResponseDTO(
         Long id,
        String ticketType,
        int quantity,
         double price
) {
}
