package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(NewBookingDto dto, Long userId);

    BookingDto update(Long bookingId, Long userId, boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getByState(String state, Long userId);

    List<BookingDto> getByOwnerAndState(String state, Long userId);
}
