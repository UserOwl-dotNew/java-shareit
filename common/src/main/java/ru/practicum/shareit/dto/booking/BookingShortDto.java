package ru.practicum.shareit.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.shareit.constants.SqlConstants.DATA_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDto {
    private Long id;
    private Long bookerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime end;
}
