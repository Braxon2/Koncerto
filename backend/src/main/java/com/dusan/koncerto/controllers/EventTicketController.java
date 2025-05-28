package com.dusan.koncerto.controllers;

import com.dusan.koncerto.service.EventTicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/event-tickets")
public class EventTicketController {

    private final EventTicketService eventTicketService;

    public EventTicketController(EventTicketService eventTicketService) {
        this.eventTicketService = eventTicketService;
    }




}
