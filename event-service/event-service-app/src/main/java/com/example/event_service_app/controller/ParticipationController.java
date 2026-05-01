package com.example.event_service_app.controller;

import com.example.event_service_app.service.ParticipationService;
import com.example.event_service_client.dto.ParticipationCreateDto;
import com.example.event_service_client.dto.ParticipationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationResponseDto register(@Valid @RequestBody ParticipationCreateDto dto) {
        return participationService.register(dto);
    }

    @GetMapping("/event/{eventId}")
    public List<ParticipationResponseDto> getByEventId(@PathVariable Long eventId) {
        return participationService.getByEventId(eventId);
    }
    @GetMapping("/my-tickets")
    public List<ParticipationResponseDto> getMyTickets() {
        return participationService.getMyTickets();
    }
}

