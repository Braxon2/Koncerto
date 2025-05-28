package com.dusan.koncerto.dto.response;

import java.time.LocalDateTime;

public record TicketDTO(
        Long id,
        String artist,
        String city,
        String address,
        String venue,
        LocalDateTime dateTime,
        String typeOfTicket,
        double price,
        String qrContent

) {
}
