package com.dusan.koncerto.model;

import jakarta.persistence.*;

@Entity
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "eventTicket_id")
    private EventTicket eventTicket;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(length = 512)
    private String qrContent;

    public Ticket() {
    }

    public Ticket(EventTicket eventTicket, User user) {
        this.eventTicket = eventTicket;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventTicket getEventTicket() {
        return eventTicket;
    }

    public void setEventTicket(EventTicket eventTicket) {
        this.eventTicket = eventTicket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }
}
