package com.example.event_service_client.client;

import com.example.event_service_client.dto.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.List;

@HttpExchange("/events")
public interface EventServiceClient {

    @PostExchange
    EventResponseDto createEvent(@RequestBody EventCreateDto dto);

    @GetExchange("/{id}")
    EventResponseDto getEventById(@PathVariable("id") Long id);

    @PutExchange("/{id}")
    EventResponseDto updateEvent(@PathVariable("id") Long id, @RequestBody EventUpdateDto dto);

    @DeleteExchange("/{id}")
    void deleteEvent(@PathVariable("id") Long id);

    @GetExchange
    List<EventResponseDto> getAllEvents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    );

    @GetExchange("/category/{categoryId}")
    List<EventResponseDto> getEventsByCategory(@PathVariable("categoryId") Long categoryId);

    @GetExchange("/city/{city}")
    List<EventResponseDto> getEventsByCity(@PathVariable("city") String city);
}

