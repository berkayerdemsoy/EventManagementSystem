package com.example.event_service_app.repository;

import com.example.event_service_app.entity.Event;
import com.example.event_service_client.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // ─── Kategorisiyle birlikte çeken versiyonlar (N+1 önlemek için) ──────────
    // EntityGraph ile aynı sorguda category JOIN edilir; response DTO'daki
    // category alanlarına erişildiğinde ek SELECT atılmaz.

    @EntityGraph(attributePaths = {"category"})
    @Override
    Optional<Event> findById(Long id);

    @EntityGraph(attributePaths = {"category"})
    @Override
    Page<Event> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByCategoryId(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByCityIgnoreCase(String city, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Şu an kullanılmıyor; gerekirse EntityGraph eklenebilir.
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    Page<Event> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.endDate < :now")
    List<Event> findByStatusAndEndDateBefore(@Param("status") EventStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.startDate <= :now AND e.endDate >= :now")
    List<Event> findByStatusAndStartDateBeforeAndEndDateAfter(@Param("status") EventStatus status, @Param("now") LocalDateTime now);
}
