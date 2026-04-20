package com.example.event_service_app.mapper;

import com.example.event_service_app.entity.Event;
import com.example.event_service_client.dto.EventCreateDto;
import com.example.event_service_client.dto.EventResponseDto;
import com.example.event_service_client.dto.EventUpdateDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface EventMapper {

    @Mapping(source = "category", target = "category")
    @Mapping(source = "currentAttendees", target = "currentAttendees")
    EventResponseDto toResponseDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "ownerEmail", ignore = true)
    @Mapping(target = "currentAttendees", constant = "0L")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(EventCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "ownerEmail", ignore = true)
    @Mapping(target = "currentAttendees", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEventFromDto(EventUpdateDto dto, @MappingTarget Event event);
}

