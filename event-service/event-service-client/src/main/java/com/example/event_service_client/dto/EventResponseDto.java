package com.example.event_service_client.dto;

import com.example.event_service_client.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long ownerId;
    private String address;
    private String city;
    private Long capacity;
    private Long currentAttendees;
    private BigDecimal price;
    private EventStatus status;
    private CategoryDto category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

