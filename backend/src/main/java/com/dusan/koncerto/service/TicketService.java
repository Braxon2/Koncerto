package com.dusan.koncerto.service;

import com.dusan.koncerto.model.EventTicket;
import com.dusan.koncerto.model.Ticket;
import com.dusan.koncerto.model.User;
import com.dusan.koncerto.repository.EventTicketRepository;
import com.dusan.koncerto.repository.TicketRepository;
import com.dusan.koncerto.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    private final EventTicketRepository eventTicketRepository;

    private final UserRepository userRepository;



    public TicketService(TicketRepository ticketRepository, EventTicketRepository eventTicketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.eventTicketRepository = eventTicketRepository;
        this.userRepository = userRepository;
    }

    public void buyTicket(Long eventId,
                          String userEmail,
                          String ticketType,
                          int quantity) throws Exception {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<EventTicket> optionalEventTicket = eventTicketRepository.findByEventIdAndTicketType(eventId,ticketType);

        if(!optionalEventTicket.isPresent()){
            throw new Exception("No such type of ticket or event");
        }


        EventTicket eventTicket = optionalEventTicket.get();


        if(eventTicket.getQuantity() - quantity < 0){
            throw new Exception("Not enough tickets");
        }


        for (int i = 0; i < quantity; i++) {
            Ticket ticket = new Ticket();
            ticket.setUser(user);
            ticket.setEventTicket(eventTicket);

            Ticket savedTicket = ticketRepository.save(ticket);

            String qrContent = "TICKET#" + savedTicket.getId()
                    + ";USER#" + user.getId()
                    + ";EVENT#" + eventTicket.getEvent().getId();

            savedTicket.setQrContent(qrContent);
            ticketRepository.save(savedTicket);

            user.getTickets().add(savedTicket);
        }


        eventTicket.setQuantity(eventTicket.getQuantity() - quantity);
        eventTicketRepository.save(eventTicket);

    }
}
