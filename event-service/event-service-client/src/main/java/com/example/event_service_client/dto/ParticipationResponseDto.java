package com.example.event_service_client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationResponseDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long participantId;
    private String participantEmail;
    private LocalDateTime registeredAt;
}

