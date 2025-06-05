package com.dusan.koncerto.service;

import com.dusan.koncerto.dto.request.EventRequestDTO;
import com.dusan.koncerto.dto.request.EventTicketRequestDTO;
import com.dusan.koncerto.dto.response.EventResponseDTO;
import com.dusan.koncerto.dto.response.EventTicketResponseDTO;
import com.dusan.koncerto.exceptions.InvalidEventDataException;
import com.dusan.koncerto.exceptions.NoSuchElementException;
import com.dusan.koncerto.exceptions.TicketExistsException;
import com.dusan.koncerto.model.Event;
import com.dusan.koncerto.model.EventTicket;
import com.dusan.koncerto.repository.EventRepository;
import com.dusan.koncerto.repository.EventTicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final EventTicketRepository eventTicketRepository;

    private final S3Service s3Service;

    public EventService(EventRepository eventRepository, EventTicketRepository eventTicketRepository, S3Service s3Service) {
        this.eventRepository = eventRepository;
        this.eventTicketRepository = eventTicketRepository;
        this.s3Service = s3Service;
    }

    public EventResponseDTO addEvent(EventRequestDTO eventDTO, MultipartFile imageFile) {

        if (imageFile == null || imageFile.isEmpty()) {
            throw new InvalidEventDataException("Event image is required. Please upload an image file.");
        }


         if (!imageFile.getContentType().startsWith("image/")) {
             throw new InvalidEventDataException("Invalid image file type. Only image files are allowed.");
         }

         if (imageFile.getSize() > 5 * 1024 * 1024) {
             throw new InvalidEventDataException("Image file size exceeds 5MB limit.");
         }

        LocalDate eventDate = eventDTO.dateTime().toLocalDate();
        LocalDateTime startOfDay = eventDate.atStartOfDay();
        LocalDateTime endOfDay = eventDate.atTime(LocalTime.MAX);


        Optional<Event> existingArtistEvent = eventRepository.findByArtistAndDateTimeBetween(
                eventDTO.artist(), startOfDay, endOfDay);
        if (existingArtistEvent.isPresent()) {
            throw new InvalidEventDataException("Artist '" + eventDTO.artist() + "' already has an event scheduled on " + eventDate + ".");
        }


        Optional<Event> existingVenueEvent = eventRepository.findByVenueAndAddressAndCityAndDateTimeBetween(
                eventDTO.venue(), eventDTO.address(), eventDTO.city(), startOfDay, endOfDay);
        if (existingVenueEvent.isPresent()) {
            throw new InvalidEventDataException("Venue '" + eventDTO.venue() + "' at " + eventDTO.address() + ", " + eventDTO.city() + " is already booked on " + eventDate + ".");
        }


        String imageUrl = s3Service.uploadFile(imageFile);


        Event event = new Event
                (eventDTO.artist(),
                        eventDTO.city(),
                        eventDTO.address(),
                        eventDTO.venue(),
                        eventDTO.dateTime(),
                        eventDTO.description(),
                        imageUrl
                );

        eventRepository.save(event);

        return new EventResponseDTO(event.getId(),
                event.getArtist(),
                event.getCity(),
                event.getAddress(),
                event.getVenue(),
                event.getDateTime(),
                event.getDescription(),
                event.getImageURL());
    }

    public EventResponseDTO getEvent(Long eventId)  {

        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new NoSuchElementException("No such event with that id.");
        }
        Event event = optionalEvent.get();
        return new EventResponseDTO(event.getId(),
                event.getArtist(),
                event.getCity(),
                event.getAddress(),
                event.getVenue(),
                event.getDateTime(),
                event.getDescription(),
                event.getImageURL()
        );


    }

    public Page<EventResponseDTO> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(event ->
                        new EventResponseDTO(
                                event.getId(),
                                event.getArtist(),
                                event.getCity(),
                                event.getAddress(),
                                event.getVenue(),
                                event.getDateTime(),
                                event.getDescription(),
                                event.getImageURL())
                );

    }

    public EventTicketResponseDTO addTicket(EventTicketRequestDTO eventTicketDTO, Long eventId)  {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new NoSuchElementException("No such event with that id.");
        }

        Event event = optionalEvent.get();

        EventTicket eventTicket = new EventTicket(
                event,
                eventTicketDTO.ticketType(),
                eventTicketDTO.quantity(),
                eventTicketDTO.price()
        );



        if(event.getEventTicketList().contains(eventTicket)){
            throw new TicketExistsException("This type of ticket already exists!");
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

    public List<EventTicketResponseDTO> getAllTickets(Long eventId)  {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new NoSuchElementException("No such event with that id.");
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

        */


        eventRepository.deleteById(eventId);
    }

    public void deleteTicketfromEvent(Long eventId, Long eventTicketId) throws Exception {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if(!optionalEvent.isPresent()){
            throw new NoSuchElementException("No such event with that id.");
        }

        Optional<EventTicket> optionalEventTicket = eventTicketRepository.findById(eventTicketId);

        if(!optionalEventTicket.isPresent()){
            throw new NoSuchElementException("No such event ticket with that id.");
        }

        Event event = optionalEvent.get();

        EventTicket eventTicket = optionalEventTicket.get();

        if(!eventTicket.getTickets().isEmpty()){
            throw new Exception("Cannot delete EventTicket: tickets have already been sold.");
        }

        event.getEventTicketList().remove(eventTicket);

        eventTicketRepository.deleteById(eventTicketId);

    }

    public EventResponseDTO updateEventImage(Long eventId, MultipartFile imageFile) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Event not found with ID: " + eventId));

        String newImageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
             if (event.getImageURL() != null && !event.getImageURL().isEmpty()) {
                s3Service.deleteFile(event.getImageURL());
             }
            newImageUrl = s3Service.uploadFile(imageFile);
        }

        event.setImageURL(newImageUrl);
        eventRepository.save(event);

        return new EventResponseDTO(
                event.getId(),
                event.getArtist(),
                event.getCity(),
                event.getAddress(),
                event.getVenue(),
                event.getDateTime(),
                event.getDescription(),
                event.getImageURL()
        );

    }
}
