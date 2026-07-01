package com.example.event_service_app.repository;

import com.example.event_service_app.entity.Event;
import com.example.event_service_client.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Event> findByCityIgnoreCase(String city, Pageable pageable);
    Page<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    Page<Event> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.endDate < :now")
    List<Event> findByStatusAndEndDateBefore(@Param("status") EventStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.startDate <= :now AND e.endDate >= :now")
    List<Event> findByStatusAndStartDateBeforeAndEndDateAfter(@Param("status") EventStatus status, @Param("now") LocalDateTime now);
}

