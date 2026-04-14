package com.example.event_service_app.repository;

import com.example.event_service_app.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Event> findByCityIgnoreCase(String city, Pageable pageable);
    Page<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    Page<Event> findByOwnerId(Long ownerId, Pageable pageable);
}

