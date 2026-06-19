package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Необходимо указать description")
    private String description;
    private LocalDateTime createdAt;
}
