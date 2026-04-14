package com.example.event_service_client.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateDto {
    private String title;
    private String description;
    private String address;
    private String city;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Long capacity;

    @DecimalMin(value = "0.0", message = "Price must be zero or positive")
    private BigDecimal price;

    private Long categoryId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

