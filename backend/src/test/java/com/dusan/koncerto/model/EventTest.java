package com.dusan.koncerto.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    @Test
    void testNoArgsConstructor() {
        // Test the no-argument constructor
        Event event = new Event();
        assertNotNull(event);
        assertNull(event.getId());
        assertNull(event.getArtist());
        assertNull(event.getCity());
        assertNull(event.getAddress());
        assertNull(event.getVenue());
        assertNull(event.getDateTime());
        assertNull(event.getDescription());
        assertNull(event.getImageURL());
        assertNull(event.getEventTicketList()); // Should be null or empty list depending on initialization
    }

    @Test
    void testAllArgsConstructor() {
        // Test the all-argument constructor
        String artist = "Test Artist";
        String city = "Test City";
        String address = "Test Address";
        String venue = "Test Venue";
        LocalDateTime dateTime = LocalDateTime.now();
        String description = "Test Description";
        String imageUrl = "http://example.com/image.jpg";

        Event event = new Event(artist, city, address, venue, dateTime, description, imageUrl);

        assertNotNull(event);
        assertEquals(artist, event.getArtist());
        assertEquals(city, event.getCity());
        assertEquals(address, event.getAddress());
        assertEquals(venue, event.getVenue());
        assertEquals(dateTime, event.getDateTime());
        assertEquals(description, event.getDescription());
        assertEquals(imageUrl, event.getImageURL());
        // eventTicketList is not part of this constructor, so it should be null
        assertNull(event.getEventTicketList());
    }

    @Test
    void testSettersAndGetters() {
        Event event = new Event();

        Long id = 1L;
        String artist = "New Artist";
        String city = "New City";
        String address = "New Address";
        String venue = "New Venue";
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 20, 19, 0);
        String description = "New Description";
        String imageUrl = "http://example.com/new_image.png";
        List<EventTicket> ticketList = new ArrayList<>();
        ticketList.add(new EventTicket()); // Add a dummy ticket for testing

        event.setId(id);
        event.setArtist(artist);
        event.setCity(city);
        event.setAddress(address);
        event.setVenue(venue);
        event.setDateTime(dateTime);
        event.setDescription(description);
        event.setImageURL(imageUrl);
        event.setEventTicketList(ticketList);

        assertEquals(id, event.getId());
        assertEquals(artist, event.getArtist());
        assertEquals(city, event.getCity());
        assertEquals(address, event.getAddress());
        assertEquals(venue, event.getVenue());
        assertEquals(dateTime, event.getDateTime());
        assertEquals(description, event.getDescription());
        assertEquals(imageUrl, event.getImageURL());
        assertEquals(ticketList, event.getEventTicketList());
        assertFalse(event.getEventTicketList().isEmpty());
    }
}