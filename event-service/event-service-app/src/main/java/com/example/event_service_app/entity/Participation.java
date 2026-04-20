package com.example.event_service_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "participations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "participant_id"})
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    /**
     * Denormalized field — User Service'e tekrar istek atmamak için
     * kayıt anında frontend/client'tan alınır ve saklanır.
     */
    @Column(name = "participant_email", nullable = false)
    private String participantEmail;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }
}

