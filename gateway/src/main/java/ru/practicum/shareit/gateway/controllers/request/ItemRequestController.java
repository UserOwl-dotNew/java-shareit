package ru.practicum.shareit.gateway.controllers.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.request.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final BaseClient client;

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody ItemRequestDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /requests - создание запроса вещи пользователем {}", userId);
        return client.post("/requests", userId, dto);
    }

    @GetMapping
    public ResponseEntity<?> getSelfRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /requests - запрос всех запросов пользователя {}, from={}, size={}", userId, from, size);
        return client.get("/requests?from=" + from + "&size=" + size, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /requests/all - запрос всех запросов, from={}, size={}", from, size);
        return client.get("/requests/all?from=" + from + "&size=" + size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/{} - запрос вещи пользователем {}", requestId, userId);
        return client.get("/requests/" + requestId, userId);
    }
}