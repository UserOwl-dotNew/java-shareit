package ru.practicum.shareit.gateway.controllers.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.booking.NewBookingDto;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String REQUEST_HEADER_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BaseClient client;

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody NewBookingDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /bookings - создание бронирования от пользователя {}", userId);
        return client.post("/bookings", userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<?> approve(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,  // ← параметр обязателен
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("PATCH /bookings/{} - подтверждение бронирования пользователем {}, approved={}",
                bookingId, userId, approved);
        return client.patch("/bookings/" + bookingId + "?approved=" + approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> get(@PathVariable("bookingId") Long bookingId,
                                 @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        log.info("GET /bookings/{} - запрос бронирования пользователем {}", bookingId, userId);
        return client.get("/bookings/" + bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<?> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                         @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        log.info("GET /bookings - запрос бронирований пользователя {} с состоянием {}", userId, state);
        return client.get("/bookings?state=" + state, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        log.info("GET /bookings/owner - запрос бронирований владельца {} с состоянием {}", userId, state);
        return client.get("/bookings/owner?state=" + state, userId);
    }
}
