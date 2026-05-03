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
import com.example.user_service_client.grpc.BeOwnerRequest;
import com.example.user_service_client.grpc.GetUserByIdRequest;
import com.example.user_service_client.grpc.UserGrpcResponse;
import com.example.user_service_client.grpc.UserGrpcServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    /**
     * gRPC BlockingStub — HTTP UserServiceClient'ın yerini aldı.
     * BlockingStub senkron çağrı yapar: mevcut Tomcat thread'inde bloklanır.
     * Bu sayede GrpcJwtClientInterceptor, RequestContextHolder'a (ThreadLocal) güvenle erişir.
     * FutureStub/async kullanılsaydı farklı thread'e geçişte token kaybolabilirdi.
     */
    private final UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userGrpcStub;
    private final NotificationEventProducer notificationEventProducer;

    // ─── Event CRUD ──────────────────────────────────────────────────────────

    @Override
    @Transactional
    public EventResponseDto createEvent(EventCreateDto dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentRole = SecurityUtils.getCurrentRole();

        UserGrpcResponse owner;
        if (!"EVENT_OWNER".equals(currentRole) && !"ADMIN".equals(currentRole)) {
            owner = getUserByIdGrpc(currentUserId);
            if (!owner.getVerified()) {
                throw new ForbiddenException("User must be verified to create events");
            }
            // BeOwner: rol günceller + güncel kullanıcıyı tek round-trip'te döner.
            // HTTP versiyonunda iki ayrı çağrı gerekliydi; gRPC'de tek çağrı yeterli.
            owner = beOwnerGrpc(currentUserId);
        } else {
            owner = getUserByIdGrpc(currentUserId);
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + dto.getCategoryId()));

        Event event = eventMapper.toEntity(dto);
        event.setOwnerId(currentUserId);
        event.setOwnerEmail(owner.getEmail());
        event.setCategory(category);

        Event savedEvent = eventRepository.save(event);

        notificationEventProducer.send(NotificationEvent.builder()
                .eventType(NotificationEventType.EVENT_OWNER_WELCOME)
                .recipientEmail(owner.getEmail())
                .payload(java.util.Map.of(
                        "ownerName", !owner.getFirstName().isBlank() ? owner.getFirstName() : owner.getUsername(),
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

    // ─── gRPC Helper Metotları ────────────────────────────────────────────────

    /**
     * getUserById gRPC çağrısı — StatusRuntimeException → uygulama exception'ına çevrilir.
     * Bilinmeyen hata kodları için RuntimeException fırlatılır; önüne global exception
     * handler yakalamayı bırak (5xx döner, client yeniden dener).
     */
    private UserGrpcResponse getUserByIdGrpc(Long userId) {
        try {
            return userGrpcStub.getUserById(
                    GetUserByIdRequest.newBuilder().setId(userId).build()
            );
        } catch (StatusRuntimeException e) {
            log.error("gRPC GetUserById başarısız — userId={}, status={}", userId, e.getStatus());
            throw switch (e.getStatus().getCode()) {
                case NOT_FOUND       -> new NotFoundException("User not found with id: " + userId);
                case PERMISSION_DENIED -> new ForbiddenException(e.getStatus().getDescription());
                default              -> new RuntimeException("gRPC error: " + e.getStatus().getDescription());
            };
        }
    }

    /**
     * beOwner gRPC çağrısı — kullanıcıyı EVENT_OWNER'a yükseltir.
     * HTTP versiyonundan farkı: beOwner + getUserById tek round-trip'te tamamlanır.
     */
    private UserGrpcResponse beOwnerGrpc(Long userId) {
        try {
            return userGrpcStub.beOwner(
                    BeOwnerRequest.newBuilder().setId(userId).build()
            );
        } catch (StatusRuntimeException e) {
            log.error("gRPC BeOwner başarısız — userId={}, status={}", userId, e.getStatus());
            throw switch (e.getStatus().getCode()) {
                case NOT_FOUND       -> new NotFoundException("User not found with id: " + userId);
                case PERMISSION_DENIED -> new ForbiddenException(e.getStatus().getDescription());
                default              -> new RuntimeException("gRPC error: " + e.getStatus().getDescription());
            };
        }
    }
}
