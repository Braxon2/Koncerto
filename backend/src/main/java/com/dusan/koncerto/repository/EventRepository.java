package com.dusan.koncerto.repository;

import com.dusan.koncerto.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
    Optional<Event> findByArtistAndDateTimeBetween(String artist, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Optional<Event> findByVenueAndDateTimeBetween(String venue, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Optional<Event> findByVenueAndAddressAndCityAndDateTimeBetween(String venue, String address, String city, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
