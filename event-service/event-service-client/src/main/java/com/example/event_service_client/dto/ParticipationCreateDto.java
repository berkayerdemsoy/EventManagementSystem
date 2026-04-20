package com.example.event_service_client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationCreateDto {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    // participantId JWT'den alınacak, ama email registration anında gerekiyor
    @NotBlank(message = "Participant email is required")
    @Email(message = "Invalid email format")
    private String participantEmail;
}

