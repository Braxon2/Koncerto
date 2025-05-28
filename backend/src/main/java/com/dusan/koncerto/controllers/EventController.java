package com.dusan.koncerto.controllers;

import com.dusan.koncerto.dto.request.BuyTicketRequestDTO;
import com.dusan.koncerto.dto.request.EventRequestDTO;
import com.dusan.koncerto.dto.request.EventTicketRequestDTO;
import com.dusan.koncerto.dto.response.EventResponseDTO;
import com.dusan.koncerto.dto.response.EventTicketResponseDTO;
import com.dusan.koncerto.service.EventService;
import com.dusan.koncerto.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    private final TicketService ticketService;

    public EventController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @PostMapping
    public EventResponseDTO addEvent(@RequestBody EventRequestDTO eventDTO){
        return eventService.addEvent(eventDTO);
    }

    @GetMapping("/{eventId}")
    public EventResponseDTO getEvent(@PathVariable Long eventId) throws Exception {
        return eventService.getEvent(eventId);
    }

    @GetMapping
    public List<EventResponseDTO> getAllEvent() throws Exception {
        return eventService.getAllEvent();
    }

    @PostMapping("/{eventId}/tickets")
    public EventTicketResponseDTO addEventTicket(@RequestBody EventTicketRequestDTO eventTicketDTO,
                                                 @PathVariable Long eventId) throws Exception {
        return eventService.addTicket(eventTicketDTO, eventId);
    }

    @GetMapping("/{eventId}/tickets")
    public List<EventTicketResponseDTO> getAllTicketsfromEvent(@PathVariable Long eventId) throws Exception {
        return eventService.getAllTickets(eventId);
    }

    @DeleteMapping("/{eventId}/tickets/{event_ticketId}")
    public List<EventTicketResponseDTO> deleteTicketfromEvent(@PathVariable Long eventId,
                                                              @PathVariable Long event_ticketId) throws Exception {
        return eventService.deleteTicketfromEvent(eventId,event_ticketId);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable Long eventId){
        eventService.deleteEvent(eventId);
    }

    @PostMapping("/{eventId}/buy")
    public ResponseEntity<?> buyTicket(
            @PathVariable Long eventId,
            @RequestBody BuyTicketRequestDTO request,
            Authentication authentication
    ) throws Exception {
        String userEmail = authentication.getName();


        ticketService.buyTicket(eventId, userEmail, request.ticketType(), request.quantity());

        return ResponseEntity.ok("Tickets purchased successfully.");
    }




}
