package com.example.event_service_app.controller;

import com.example.event_service_app.service.EventService;
import com.example.event_service_client.dto.EventCreateDto;
import com.example.event_service_client.dto.EventResponseDto;
import com.example.event_service_client.dto.EventUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateDto dto) {
        EventResponseDto event = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        EventResponseDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id,
                                                        @Valid @RequestBody EventUpdateDto dto) {
        EventResponseDto event = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(Pageable pageable) {
        Page<EventResponseDto> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<EventResponseDto>> getEventsByCategory(@PathVariable Long categoryId,
                                                                       Pageable pageable) {
        Page<EventResponseDto> events = eventService.getEventsByCategory(categoryId, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<Page<EventResponseDto>> getEventsByCity(@PathVariable String city,
                                                                   Pageable pageable) {
        Page<EventResponseDto> events = eventService.getEventsByCity(city, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<EventResponseDto>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Pageable pageable) {
        Page<EventResponseDto> events = eventService.getEventsByDateRange(start, end, pageable);
        return ResponseEntity.ok(events);
    }
}

