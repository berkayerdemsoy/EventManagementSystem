package com.example.event_service_app.service;

import com.example.event_service_client.dto.EventCreateDto;
import com.example.event_service_client.dto.EventResponseDto;
import com.example.event_service_client.dto.EventUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EventService {
    EventResponseDto createEvent(EventCreateDto dto);
    EventResponseDto getEventById(Long id);
    EventResponseDto updateEvent(Long id, EventUpdateDto dto);
    void deleteEvent(Long id);
    Page<EventResponseDto> getAllEvents(Pageable pageable);
    Page<EventResponseDto> getEventsByCategory(Long categoryId, Pageable pageable);
    Page<EventResponseDto> getEventsByCity(String city, Pageable pageable);
    Page<EventResponseDto> getEventsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);
}

