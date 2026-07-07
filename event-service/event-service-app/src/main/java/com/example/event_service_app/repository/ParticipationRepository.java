package com.example.event_service_app.repository;

import com.example.event_service_app.entity.Participation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);

    @Modifying
    @Query("DELETE FROM Participation p WHERE p.event.id = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);

    // ─── Event'iyle birlikte çeken versiyonlar (N+1 önlemek için) ─────────────
    // EntityGraph ile aynı sorguda event JOIN edilir; response DTO'daki
    // eventTitle alanına erişildiğinde ek SELECT atılmaz.

    @EntityGraph(attributePaths = {"event"})
    List<Participation> findByEventId(Long eventId);

    @EntityGraph(attributePaths = {"event"})
    List<Participation> findByParticipantId(Long participantId);

    /**
     * Reminder job için: başlangıcı [now+23h, now+25h] arasında olan
     * eventlerin henüz reminder gönderilmemiş participations'larını döner.
     * JOIN FETCH ile event ilişkisi tek sorguda yüklenir (N+1 önlenir).
     */
    @Query("""
            SELECT p FROM Participation p
            JOIN FETCH p.event e
            WHERE e.startDate BETWEEN :from AND :to
              AND p.reminderSent = false
            """)
    List<Participation> findPendingReminders(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

}
