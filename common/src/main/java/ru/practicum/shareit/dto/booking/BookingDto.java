package ru.practicum.shareit.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.enums.booking.BookingStatus;

import java.time.LocalDateTime;

import static ru.practicum.shareit.constants.Constants.DATA_PATTERN;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDto {
    private Long id;
    private ItemDto item;
    private UserDto booker;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime end;
    private BookingStatus status;
}
