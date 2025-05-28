package com.dusan.koncerto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    private String artist;

    private String city;

    private String address;

    private String venue;

    private LocalDateTime dateTime;

    private String description;

    @OneToMany(mappedBy = "event")
    private List<EventTicket> eventTicketList;

    public Event() {
    }

    public Event(String artist, String city, String address, String venue, LocalDateTime dateTime, String description) {
        this.artist = artist;
        this.city = city;
        this.address = address;
        this.venue = venue;
        this.dateTime = dateTime;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EventTicket> getEventTicketList() {
        return eventTicketList;
    }

    public void setEventTicketList(List<EventTicket> eventTicketList) {
        this.eventTicketList = eventTicketList;
    }
}
