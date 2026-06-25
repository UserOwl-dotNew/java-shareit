package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String REQUEST_HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto add(@Valid @RequestBody NewBookingDto dto,
                          @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        return bookingService.create(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto update(@PathVariable("bookingId") Long bookingId,
                             @RequestParam boolean approved,
                             @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto get(@PathVariable("bookingId") Long bookingId,
                          @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                        @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        return bookingService.getByState(state, userId);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        return bookingService.getByOwnerAndState(state, userId);
    }
}
