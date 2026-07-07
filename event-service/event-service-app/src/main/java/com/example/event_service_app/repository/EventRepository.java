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

    @EntityGraph(attributePaths = {"category"})
    Optional<Event> findByIdWithCategory(Long id);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findAllWithCategory(Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByCategoryIdWithCategory(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByCityIgnoreCaseWithCategory(String city, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByStartDateBetweenWithCategory(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByStatusWithCategory(EventStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Event> findByOwnerIdWithCategory(Long ownerId, Pageable pageable);

    // ─── Temel versiyonlar (kategori ihtiyaç edilmeyen yerlerde kullanılır) ───

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

