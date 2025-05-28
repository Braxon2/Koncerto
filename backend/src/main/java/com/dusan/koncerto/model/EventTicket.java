package com.dusan.koncerto.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class EventTicket {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String ticketType;

    private int quantity;

    private double price;

    @OneToMany(mappedBy = "eventTicket")
    private List<Ticket> tickets;

    public EventTicket() {
    }

    public EventTicket(Event event, String ticketType, int quantity, double price) {
        this.event = event;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticket_type) {
        this.ticketType = ticket_type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventTicket that = (EventTicket) o;
        return Objects.equals(ticketType, that.ticketType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ticketType);
    }
}
