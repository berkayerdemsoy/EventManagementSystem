package com.example.event_service_app.repository;

import com.example.event_service_app.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);

    List<Participation> findByEventId(Long eventId);

    /**
     * Reminder job için: başlangıcı [now+23h, now+25h] arasında olan
     * eventlerin henüz reminder gönderilmemiş participations'larını döner.
     */
    @Query("""
            SELECT p FROM Participation p
            JOIN p.event e
            WHERE e.startDate BETWEEN :from AND :to
              AND p.reminderSent = false
            """)
    List<Participation> findPendingReminders(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}

