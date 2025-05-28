package com.dusan.koncerto.service;

import com.dusan.koncerto.dto.request.EventRequestDTO;
import com.dusan.koncerto.dto.request.EventTicketRequestDTO;
import com.dusan.koncerto.dto.response.EventResponseDTO;
import com.dusan.koncerto.dto.response.EventTicketResponseDTO;
import com.dusan.koncerto.model.Event;
import com.dusan.koncerto.model.EventTicket;
import com.dusan.koncerto.repository.EventRepository;
import com.dusan.koncerto.repository.EventTicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final EventTicketRepository eventTicketRepository;

    public EventService(EventRepository eventRepository, EventTicketRepository eventTicketRepository) {
        this.eventRepository = eventRepository;
        this.eventTicketRepository = eventTicketRepository;
    }

    public EventResponseDTO addEvent(EventRequestDTO eventDTO) {
        Event event = new Event
                (eventDTO.artist(),
                        eventDTO.city(),
                        eventDTO.address(),
                        eventDTO.venue(),
                        eventDTO.dateTime(),
                        eventDTO.description());

        eventRepository.save(event);

        return new EventResponseDTO(event.getId(),
                event.getArtist(),
                event.getCity(),
                event.getAddress(),
                event.getVenue(),
                event.getDateTime(),
                event.getDescription());
    }


    public EventResponseDTO getEvent(Long eventId) throws Exception {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new Exception("No such event with that id.");
        }
        Event event = optionalEvent.get();
        return new EventResponseDTO(event.getId(),
                event.getArtist(),
                event.getCity(),
                event.getAddress(),
                event.getVenue(),
                event.getDateTime(),
                event.getDescription()
        );


    }

    public List<EventResponseDTO> getAllEvent() {
        return eventRepository.findAll()
                .stream()
                .map(event ->
                        new EventResponseDTO(
                                event.getId(),
                                event.getArtist(),
                                event.getCity(),
                                event.getAddress(),
                                event.getVenue(),
                                event.getDateTime(),
                                event.getDescription())
                )
                .toList();

    }

    public EventTicketResponseDTO addTicket(EventTicketRequestDTO eventTicketDTO, Long eventId) throws Exception {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new Exception("No such event with that id.");
        }

        Event event = optionalEvent.get();

        EventTicket eventTicket = new EventTicket(
                event,
                eventTicketDTO.ticketType(),
                eventTicketDTO.quantity(),
                eventTicketDTO.price()
        );



        if(event.getEventTicketList().contains(eventTicket)){
            throw new Exception("This type of ticket already exists!");
        }
        event.getEventTicketList().add(eventTicket);

        eventRepository.save(event);

        eventTicketRepository.save(eventTicket);

        return new EventTicketResponseDTO(
                eventTicket.getId(),
                eventTicket.getTicketType(),
                eventTicket.getQuantity(),
                eventTicket.getPrice()
        );

    }

    public List<EventTicketResponseDTO> getAllTickets(Long eventId) throws Exception {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new Exception("No such event with that id.");
        }

        Event event = optionalEvent.get();

        return event.getEventTicketList()
                .stream()
                .map(ticket ->
                        new EventTicketResponseDTO(
                                ticket.getId(),
                                ticket.getTicketType(),
                                ticket.getQuantity(),
                                ticket.getPrice()
                        )
                )
                .toList();
    }

    public void deleteEvent(Long eventId) {

        /*
        napraviti proveru da li ima vec kupuljenih karata ili ima vec tip karte napravljen


        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new Exception("No such event with that id.");
        }

        Event event = optionalEvent.get();

        if(event.getEventTicketList().size() > 0 || ){
            throw new Exception("Cannot delete event. Tickets have already been sold.");
        }

        */


        eventRepository.deleteById(eventId);
    }

    public List<EventTicketResponseDTO> deleteTicketfromEvent(Long eventId, Long eventTicketId) throws Exception {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new Exception("No such event with that id.");
        }

        Optional<EventTicket> optionalEventTicket = eventTicketRepository.findById(eventTicketId);

        if(!optionalEventTicket.isPresent()){
            throw new Exception("No such event ticket with that id.");
        }



        Event event = optionalEvent.get();

        EventTicket eventTicket = optionalEventTicket.get();

        if(!eventTicket.getTickets().isEmpty()){
            throw new Exception("Cannot delete EventTicket: tickets have already been sold.");
        }

        event.getEventTicketList().remove(eventTicket);

        eventTicketRepository.deleteById(eventTicketId);

        return event.getEventTicketList()
                .stream()
                .map(ticket ->
                        new EventTicketResponseDTO(
                                ticket.getId(),
                                ticket.getTicketType(),
                                ticket.getQuantity(),
                                ticket.getPrice()
                        )
                )
                .toList();


    }
}
