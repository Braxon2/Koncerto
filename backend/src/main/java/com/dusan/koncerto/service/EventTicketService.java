package com.dusan.koncerto.service;

import com.dusan.koncerto.repository.EventTicketRepository;
import org.springframework.stereotype.Service;

@Service
public class EventTicketService {

    private final EventTicketRepository eventTicketRepository;

    public EventTicketService(EventTicketRepository eventTicketRepository) {
        this.eventTicketRepository = eventTicketRepository;
    }
}
