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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventTicketRepository eventTicketRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile mockImageFile;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private EventRequestDTO testEventRequestDTO;
    private EventTicket testEventTicket;
    private EventTicketRequestDTO testEventTicketRequestDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        testEvent = new Event(
                "Artist Name", "City", "Address", "Venue", now, "Description", "http://image.url/test.jpg"
        );
        testEvent.setId(1L);

        testEventRequestDTO = new EventRequestDTO(
                "Artist Name", "City", "Address", "Venue", now, "Description"
        );

        testEventTicket = new EventTicket(testEvent, "VIP", 100, 50.0);
        testEventTicket.setId(10L);

        testEventTicketRequestDTO = new EventTicketRequestDTO("VIP", 100, 50.0);
    }

    @Test
    void addEvent() {
        when(mockImageFile.isEmpty()).thenReturn(false);
        when(mockImageFile.getContentType()).thenReturn("image/jpeg");
        when(mockImageFile.getSize()).thenReturn(1024L);

        when(eventRepository.findByArtistAndDateTimeBetween(
                testEventRequestDTO.artist(),
                testEvent.getDateTime().toLocalDate().atStartOfDay(),
                testEvent.getDateTime().toLocalDate().atTime(LocalTime.MAX))
        ).thenReturn(Optional.empty());

        when(eventRepository.findByVenueAndAddressAndCityAndDateTimeBetween(
                testEventRequestDTO.venue(),
                testEventRequestDTO.address(),
                testEventRequestDTO.city(),
                testEventRequestDTO.dateTime().toLocalDate().atStartOfDay(),
                testEventRequestDTO.dateTime().toLocalDate().atTime(LocalTime.MAX))
        ).thenReturn(Optional.empty());

        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("http://new.image.url/uploaded.jpg");

        doAnswer(new Answer<Event>() {
            @Override
            public Event answer(InvocationOnMock invocation) throws Throwable {
                Event eventArg = (Event) invocation.getArgument(0);
                eventArg.setId(testEvent.getId());
                return eventArg;
            }
        }).when(eventRepository).save(any(Event.class));


        EventResponseDTO response = eventService.addEvent(testEventRequestDTO, mockImageFile);


        assertNotNull(response);
        assertEquals(testEvent.getId(), response.id());
        assertEquals(testEventRequestDTO.artist(), response.artist());
        verify(s3Service, times(1)).uploadFile(mockImageFile);
        verify(eventRepository, times(1)).save(any(Event.class));

    }

    @Test
    void addEvent_ThrowsInvalidEventDataException_WhenImageFileIsNull() {
        assertThrows(InvalidEventDataException.class, () -> eventService.addEvent(testEventRequestDTO, null));
        verify(s3Service, never()).uploadFile(any(MultipartFile.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void addEvent_ThrowsInvalidEventDataException_WhenFileIsNotImage() {

        when(mockImageFile.isEmpty()).thenReturn(false);
        when(mockImageFile.getContentType()).thenReturn("application/pdf");

        assertThrows(InvalidEventDataException.class, () -> eventService.addEvent(testEventRequestDTO, mockImageFile));
        verify(s3Service, never()).uploadFile(any(MultipartFile.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void addEvent_ThrowsInvalidEventDataException_WhenFileIsTooBig() {

        when(mockImageFile.isEmpty()).thenReturn(false);
        when(mockImageFile.getContentType()).thenReturn("image/jpeg");
        when(mockImageFile.getSize()).thenReturn(6 * 1024 * 1024L);

        assertThrows(InvalidEventDataException.class, () -> eventService.addEvent(testEventRequestDTO, mockImageFile));
        verify(s3Service, never()).uploadFile(any(MultipartFile.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void addEvent_ThrowsInvalidEventDataException_ArtsistHaveConcertSameDay() {

        when(mockImageFile.isEmpty()).thenReturn(false);
        when(mockImageFile.getContentType()).thenReturn("image/jpeg");
        when(mockImageFile.getSize()).thenReturn(1024L);

        when(eventRepository.findByArtistAndDateTimeBetween(
                testEventRequestDTO.artist(),
                testEvent.getDateTime().toLocalDate().atStartOfDay(),
                testEvent.getDateTime().toLocalDate().atTime(LocalTime.MAX))
        ).thenReturn(Optional.of(testEvent));

        assertThrows(InvalidEventDataException.class, () -> eventService.addEvent(testEventRequestDTO, mockImageFile));
        verify(s3Service, never()).uploadFile(any(MultipartFile.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void addEvent_ThrowsInvalidEventDataException_VenueHasConcert() {

        when(mockImageFile.isEmpty()).thenReturn(false);
        when(mockImageFile.getContentType()).thenReturn("image/jpeg");
        when(mockImageFile.getSize()).thenReturn(1024L);

        when(eventRepository.findByArtistAndDateTimeBetween(
                testEventRequestDTO.artist(),
                testEvent.getDateTime().toLocalDate().atStartOfDay(),
                testEvent.getDateTime().toLocalDate().atTime(LocalTime.MAX))
        ).thenReturn(Optional.empty());

        when(eventRepository.findByVenueAndAddressAndCityAndDateTimeBetween(
                testEventRequestDTO.venue(),
                testEventRequestDTO.address(),
                testEventRequestDTO.city(),
                testEventRequestDTO.dateTime().toLocalDate().atStartOfDay(),
                testEventRequestDTO.dateTime().toLocalDate().atTime(LocalTime.MAX))
        ).thenReturn(Optional.of(testEvent));

        assertThrows(InvalidEventDataException.class, () -> eventService.addEvent(testEventRequestDTO, mockImageFile));
        verify(s3Service, never()).uploadFile(any(MultipartFile.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void getEvent() {
        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));

        EventResponseDTO response = eventService.getEvent(testEvent.getId());

        assertNotNull(response);
        assertEquals(testEvent.getId(), response.id());
        assertEquals(testEvent.getArtist(), response.artist());
        verify(eventRepository, times(1)).findById(testEvent.getId());
    }

    @Test
    void getEvent_NoSuchElementException() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(99L));
        verify(eventRepository, times(1)).findById(99L);
    }

    @Test
    void getAllEvents() {
        List<Event> events = Collections.singletonList(testEvent);
        Page<Event> eventPage = new PageImpl<>(events, PageRequest.of(0, 8), events.size());
        when(eventRepository.findAll(any(Pageable.class))).thenReturn(eventPage);

        Page<EventResponseDTO> responsePage = eventService.getAllEvents(PageRequest.of(0, 8));


        assertNotNull(responsePage);
        assertFalse(responsePage.isEmpty());
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(testEvent.getId(), responsePage.getContent().get(0).id());
        verify(eventRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void addTicket_Success() {
        testEvent.setEventTicketList(new ArrayList<>()); // Ensure the list is initialized
        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventTicketRepository.save(any(EventTicket.class))).thenReturn(testEventTicket);

        EventTicketResponseDTO response = eventService.addTicket(testEventTicketRequestDTO, testEvent.getId());

        assertNotNull(response);
        assertEquals(testEventTicketRequestDTO.ticketType(), response.ticketType());
        assertEquals(testEventTicketRequestDTO.quantity(), response.quantity());
        assertEquals(testEventTicketRequestDTO.price(), response.price());
        verify(eventRepository, times(1)).findById(testEvent.getId());
        verify(eventRepository, times(1)).save(testEvent);
        verify(eventTicketRepository, times(1)).save(any(EventTicket.class));
        assertFalse(testEvent.getEventTicketList().isEmpty());
    }

    @Test
    void addTicket_ThrowsNoSuchElementException_WhenEventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.addTicket(testEventTicketRequestDTO, 99L));
        verify(eventRepository, times(1)).findById(99L);
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventTicketRepository, never()).save(any(EventTicket.class));
    }

    @Test
    void addTicket_ThrowsTicketExistsException_WhenTicketTypeAlreadyExists() {
        testEvent.setEventTicketList(new ArrayList<>(Collections.singletonList(testEventTicket)));
        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));

        assertThrows(TicketExistsException.class, () -> eventService.addTicket(testEventTicketRequestDTO, testEvent.getId()));
        verify(eventRepository, times(1)).findById(testEvent.getId());
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventTicketRepository, never()).save(any(EventTicket.class));
    }

    @Test
    void getAllTickets() {
        testEvent.setEventTicketList(new ArrayList<>(Collections.singletonList(testEventTicket)));
        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));

        List<EventTicketResponseDTO> responseList = eventService.getAllTickets(testEvent.getId());

        assertNotNull(responseList);
        assertFalse(responseList.isEmpty());
        assertEquals(1, responseList.size());
        assertEquals(testEventTicket.getTicketType(), responseList.get(0).ticketType());
        verify(eventRepository, times(1)).findById(testEvent.getId());
    }

    @Test
    void deleteTicketfromEvent_ThrowsNoSuchElementException_WhenEventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.deleteTicketfromEvent(99L, testEventTicket.getId()));
        verify(eventRepository, times(1)).findById(99L);
        verify(eventTicketRepository, never()).findById(anyLong());
        verify(eventTicketRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteTicketfromEvent_ThrowsNoSuchElementException_WhenEventTicketNotFound() {
        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(eventTicketRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.deleteTicketfromEvent(testEvent.getId(), 99L));
        verify(eventRepository, times(1)).findById(testEvent.getId());
        verify(eventTicketRepository, times(1)).findById(99L);
        verify(eventTicketRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteTicketfromEvent_ThrowsException_WhenTicketsAlreadySold() {

        testEvent.setEventTicketList(new ArrayList<>(Collections.singletonList(testEventTicket)));
        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(eventTicketRepository.findById(testEventTicket.getId())).thenReturn(Optional.of(testEventTicket));
        // Simulate tickets being sold
        testEventTicket.setTickets(Collections.singletonList(mock(com.dusan.koncerto.model.Ticket.class))); // Assuming Ticket class


        Exception exception = assertThrows(Exception.class, () -> eventService.deleteTicketfromEvent(testEvent.getId(), testEventTicket.getId()));
        assertEquals("Cannot delete EventTicket: tickets have already been sold.", exception.getMessage());
        verify(eventRepository, times(1)).findById(testEvent.getId());
        verify(eventTicketRepository, times(1)).findById(testEventTicket.getId());
        verify(eventTicketRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateEventImage_Success_NewImage() throws IOException, IOException {

        when(mockImageFile.isEmpty()).thenReturn(false);
        when(mockImageFile.getContentType()).thenReturn("image/jpeg");
        when(mockImageFile.getSize()).thenReturn(1000L); // Less than 5MB
        when(mockImageFile.getInputStream()).thenReturn(mock(java.io.InputStream.class));

        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("http://image.url/test.jpeg");
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent); // Mock save to return the updated event


        EventResponseDTO response = eventService.updateEventImage(testEvent.getId(), mockImageFile);

        assertNotNull(response);
        assertEquals("http://image.url/test.jpeg", response.imageURL());
        verify(eventRepository, times(1)).findById(testEvent.getId());

        verify(s3Service, times(1)).deleteFile(testEvent.getImageURL());
        verify(s3Service, times(1)).uploadFile(mockImageFile);
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void updateEventImage_Success_NoExistingImage() throws IOException {

        lenient().when(mockImageFile.isEmpty()).thenReturn(false);
        lenient().when(mockImageFile.getContentType()).thenReturn("image/jpeg");
        lenient().when(mockImageFile.getSize()).thenReturn(1000L); // Less than 5MB
        lenient().when(mockImageFile.getInputStream()).thenReturn(mock(java.io.InputStream.class));

        testEvent.setImageURL(null);
        when(eventRepository.findById(testEvent.getId())).thenReturn(Optional.of(testEvent));
        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("http://new.image.url/uploaded.jpg");
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        EventResponseDTO response = eventService.updateEventImage(testEvent.getId(), mockImageFile);

        assertNotNull(response);
        assertEquals("http://new.image.url/uploaded.jpg", response.imageURL());
        verify(eventRepository, times(1)).findById(testEvent.getId());

        verify(s3Service, never()).deleteFile(anyString());
        verify(s3Service, times(1)).uploadFile(mockImageFile);
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void updateEventImage_ThrowsNoSuchElementException_WhenEventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.updateEventImage(99L, mockImageFile));
        verify(eventRepository, times(1)).findById(99L);
        verify(s3Service, never()).uploadFile(any(MultipartFile.class));
        verify(s3Service, never()).deleteFile(anyString());
        verify(eventRepository, never()).save(any(Event.class));
    }


}