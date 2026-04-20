package com.example.event_service_app.serviceImpl;

import com.example.ems_common.dto.NotificationEvent;
import com.example.ems_common.dto.NotificationEventType;
import com.example.ems_common.exceptions.ForbiddenException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.ems_common.security.SecurityUtils;
import com.example.event_service_app.entity.Category;
import com.example.event_service_app.entity.Event;
import com.example.event_service_app.kafka.NotificationEventProducer;
import com.example.event_service_app.mapper.EventMapper;
import com.example.event_service_app.repository.CategoryRepository;
import com.example.event_service_app.repository.EventRepository;
import com.example.event_service_app.service.EventService;
import com.example.event_service_client.dto.EventCreateDto;
import com.example.event_service_client.dto.EventResponseDto;
import com.example.event_service_client.dto.EventUpdateDto;
import com.example.user_service_client.client.UserServiceClient;
import com.example.user_service_client.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final UserServiceClient userServiceClient;
    private final NotificationEventProducer notificationEventProducer;

    @Override
    @Transactional
    public EventResponseDto createEvent(EventCreateDto dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentRole();

        // If user is not EVENT_OWNER and not ADMIN, check verification and promote to owner
        UserResponseDto owner;
        if (!"EVENT_OWNER".equals(currentRole) && !"ADMIN".equals(currentRole)) {
            owner = userServiceClient.getUserById(currentUserId);
            if (!owner.isVerified()) {
                throw new ForbiddenException("User must be verified to create events");
            }
            // Auto-promote to EVENT_OWNER
            userServiceClient.beOwner(currentUserId);
        } else {
            owner = userServiceClient.getUserById(currentUserId);
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + dto.getCategoryId()));

        Event event = eventMapper.toEntity(dto);
        event.setOwnerId(currentUserId);
        event.setOwnerEmail(owner.getEmail());
        event.setCategory(category);

        Event savedEvent = eventRepository.save(event);

        // Kafka: EVENT_OWNER_WELCOME
        notificationEventProducer.send(NotificationEvent.builder()
                .eventType(NotificationEventType.EVENT_OWNER_WELCOME)
                .recipientEmail(owner.getEmail())
                .payload(java.util.Map.of(
                        "ownerName", owner.getFirstName() != null ? owner.getFirstName() : owner.getUsername(),
                        "eventTitle", savedEvent.getTitle(),
                        "eventId", String.valueOf(savedEvent.getId())
                ))
                .build());

        return eventMapper.toResponseDto(savedEvent);
    }

    @Override
    public EventResponseDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
        return eventMapper.toResponseDto(event);
    }

    @Override
    @Transactional
    public EventResponseDto updateEvent(Long id, EventUpdateDto dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentRole();

        if (!"ADMIN".equals(currentRole) && !event.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException("You are not authorized to update this event");
        }

        eventMapper.updateEventFromDto(dto, event);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + dto.getCategoryId()));
            event.setCategory(category);
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toResponseDto(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentRole();

        if (!"ADMIN".equals(currentRole) && !event.getOwnerId().equals(currentUserId)) {
            throw new ForbiddenException("You are not authorized to delete this event");
        }

        eventRepository.delete(event);
    }

    @Override
    public Page<EventResponseDto> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable).map(eventMapper::toResponseDto);
    }

    @Override
    public Page<EventResponseDto> getEventsByCategory(Long categoryId, Pageable pageable) {
        return eventRepository.findByCategoryId(categoryId, pageable).map(eventMapper::toResponseDto);
    }

    @Override
    public Page<EventResponseDto> getEventsByCity(String city, Pageable pageable) {
        return eventRepository.findByCityIgnoreCase(city, pageable).map(eventMapper::toResponseDto);
    }

    @Override
    public Page<EventResponseDto> getEventsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return eventRepository.findByStartDateBetween(start, end, pageable).map(eventMapper::toResponseDto);
    }
}


