package com.dusan.koncerto.service;

import com.dusan.koncerto.dto.response.TicketDTO;
import com.dusan.koncerto.model.Event;
import com.dusan.koncerto.model.EventTicket;
import com.dusan.koncerto.model.Ticket;
import com.dusan.koncerto.model.User;
import com.dusan.koncerto.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<TicketDTO> getAllTickets(Long userId) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);

        if(!optionalUser.isPresent()){
            throw new Exception("User does not exist with this ID.");
        }

        User user = optionalUser.get();



        return user.getTickets().
                stream().
                map(ticket -> {
                    EventTicket eventTicket = ticket.getEventTicket();
                    Event event = eventTicket.getEvent();
                    return new TicketDTO(
                            ticket.getId(),
                            event.getArtist(),
                            event.getCity(),
                            event.getAddress(),
                            event.getVenue(),
                            event.getDateTime(),
                            eventTicket.getTicketType(),
                            eventTicket.getPrice(),
                            ticket.getQrContent());
                }).toList();
    }
}
