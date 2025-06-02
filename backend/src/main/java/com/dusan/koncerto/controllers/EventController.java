package com.dusan.koncerto.controllers;

import com.dusan.koncerto.dto.request.BuyTicketRequestDTO;
import com.dusan.koncerto.dto.request.EventRequestDTO;
import com.dusan.koncerto.dto.request.EventTicketRequestDTO;
import com.dusan.koncerto.dto.response.EventResponseDTO;
import com.dusan.koncerto.dto.response.EventTicketResponseDTO;
import com.dusan.koncerto.service.EventService;
import com.dusan.koncerto.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    public ResponseEntity<EventResponseDTO> createEvent(
            @RequestParam("artist") String artist,
            @RequestParam("city") String city,
            @RequestParam("address") String address,
            @RequestParam("venue") String venue,
            @RequestParam("dateTime") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam("description") String description,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        EventRequestDTO eventRequestDTO = new EventRequestDTO(artist, city, address, venue, dateTime, description);
        EventResponseDTO response = eventService.addEvent(eventRequestDTO, imageFile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{eventId}/image")
    public ResponseEntity<EventResponseDTO> updateEventImage(
            @PathVariable Long eventId,
            @RequestPart("imageFile") MultipartFile imageFile) {
        try {
            EventResponseDTO updatedEvent = eventService.updateEventImage(eventId, imageFile);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable Long eventId) throws Exception {
        EventResponseDTO event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> getAllEvents(
            @PageableDefault(size = 10, page = 0, sort = "dateTime") Pageable pageable) {
        Page<EventResponseDTO> eventsPage = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(eventsPage);
    }

    @PostMapping("/{eventId}/tickets")
    public EventTicketResponseDTO addEventTicket(
            @RequestBody EventTicketRequestDTO eventTicketDTO,
            @PathVariable Long eventId
    ) throws Exception {
        return eventService.addTicket(eventTicketDTO, eventId);
    }

    @GetMapping("/{eventId}/tickets")
    public List<EventTicketResponseDTO> getAllTicketsfromEvent(@PathVariable Long eventId) throws Exception {
        return eventService.getAllTickets(eventId);
    }

    @DeleteMapping("/{eventId}/tickets/{event_ticketId}")
    public void deleteTicketfromEvent(
            @PathVariable Long eventId,
            @PathVariable Long event_ticketId
    ) throws Exception {
         eventService.deleteTicketfromEvent(eventId,event_ticketId);
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
