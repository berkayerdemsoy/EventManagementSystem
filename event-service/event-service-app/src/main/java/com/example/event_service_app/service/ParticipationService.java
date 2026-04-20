package com.example.event_service_app.service;

import com.example.event_service_client.dto.ParticipationCreateDto;
import com.example.event_service_client.dto.ParticipationResponseDto;

import java.util.List;

public interface ParticipationService {
    ParticipationResponseDto register(ParticipationCreateDto dto);
    List<ParticipationResponseDto> getByEventId(Long eventId);
}

