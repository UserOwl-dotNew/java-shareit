package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class NewBookingDto {
    @NotNull(message = "Поле itemId не должно быть пустым")
    private Long itemId;

    @NotNull(message = "Поле start не должно быть пустым")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Поле end не должно быть пустым")
    @Future
    private LocalDateTime end;
}
