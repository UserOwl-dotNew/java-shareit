package ru.practicum.shareit.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

import static ru.practicum.shareit.constants.SqlConstants.DATA_PATTERN;

@Data
@ToString
public class NewBookingDto {
    @NotNull(message = "Поле itemId не должно быть пустым")
    private Long itemId;

    @NotNull(message = "Поле start не должно быть пустым")
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime start;

    @NotNull(message = "Поле end не должно быть пустым")
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime end;
}
