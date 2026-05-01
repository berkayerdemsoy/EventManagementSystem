package com.example.event_service_app.serviceImpl;

import com.example.ems_common.dto.NotificationEvent;
import com.example.ems_common.dto.NotificationEventType;
import com.example.ems_common.exceptions.AlreadyExistsException;
import com.example.ems_common.exceptions.CannotJoinOwnEventException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.ems_common.security.SecurityUtils;
import com.example.event_service_app.entity.Event;
import com.example.event_service_app.entity.Participation;
import com.example.event_service_app.kafka.NotificationEventProducer;
import com.example.event_service_app.repository.EventRepository;
import com.example.event_service_app.repository.ParticipationRepository;
import com.example.event_service_app.service.ParticipationService;
import com.example.event_service_client.dto.ParticipationCreateDto;
import com.example.event_service_client.dto.ParticipationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final NotificationEventProducer notificationEventProducer;

    @Override
    @Transactional
    public ParticipationResponseDto register(ParticipationCreateDto dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + dto.getEventId()));

        if (event.getOwnerId().equals(currentUserId)) {
            throw new CannotJoinOwnEventException("You cannot join an event that you have created");
        }

        if (participationRepository.existsByEventIdAndParticipantId(dto.getEventId(), currentUserId)) {
            throw new AlreadyExistsException("Already registered for this event");
        }

        Participation participation = new Participation();
        participation.setEvent(event);
        participation.setParticipantId(currentUserId);
        participation.setParticipantEmail(dto.getParticipantEmail());

        // currentAttendees artır
        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        eventRepository.save(event);

        Participation saved = participationRepository.save(participation);

        // Kafka: PARTICIPANT_REGISTERED
        notificationEventProducer.send(NotificationEvent.builder()
                .eventType(NotificationEventType.PARTICIPANT_REGISTERED)
                .recipientEmail(dto.getParticipantEmail())
                .payload(Map.of(
                        "eventTitle", event.getTitle(),
                        "eventDate", event.getStartDate().toString(),
                        "eventCity", event.getCity(),
                        "eventId", String.valueOf(event.getId())
                ))
                .build());

        return toResponseDto(saved);
    }

    @Override
    public List<ParticipationResponseDto> getByEventId(Long eventId) {
        return participationRepository.findByEventId(eventId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public List<ParticipationResponseDto> getMyTickets() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return participationRepository.findByParticipantId(currentUserId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private ParticipationResponseDto toResponseDto(Participation p) {
        ParticipationResponseDto dto = new ParticipationResponseDto();
        dto.setId(p.getId());
        dto.setEventId(p.getEvent().getId());
        dto.setEventTitle(p.getEvent().getTitle());
        dto.setParticipantId(p.getParticipantId());
        dto.setParticipantEmail(p.getParticipantEmail());
        dto.setRegisteredAt(p.getRegisteredAt());
        return dto;
    }
}

