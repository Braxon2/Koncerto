package com.dusan.koncerto.repository;

import com.dusan.koncerto.model.EventTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTicketRepository extends JpaRepository<EventTicket,Long> {
    Optional<EventTicket> findByEventIdAndTicketType(Long eventId, String ticketType);
}
