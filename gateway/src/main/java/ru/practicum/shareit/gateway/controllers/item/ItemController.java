package ru.practicum.shareit.gateway.controllers.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.comment.NewCommentDto;
import ru.practicum.shareit.dto.item.NewItemDto;

import static ru.practicum.shareit.constants.SqlConstants.REQUEST_HEADER_SHARER_USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final BaseClient client;

    @PostMapping
    public ResponseEntity<?> add(
            @Valid @RequestBody NewItemDto dto,
            @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        log.info("POST /items - запрос на добавление вещи от пользователя {}", userId);
        return client.post("/items", userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> update(
            @PathVariable Long itemId,
            @RequestBody NewItemDto dto,
            @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId
    ) {
        log.info("PATCH /items/{} - запрос на обновление вещи пользователем {}", itemId, userId);
        return client.patch("/items/" + itemId, userId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getById(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items/{} - запрос вещи пользователем {}", itemId, userId);
        return client.get("/items/" + itemId, userId);
    }

    @GetMapping
    public ResponseEntity<?> getByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /items - запрос всех вещей владельца {}, from={}, size={}", userId, from, size);
        return client.get("/items?from=" + from + "&size=" + size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /items/search - поиск вещей по тексту '{}', from={}, size={}", text, from, size);
        return client.get("/items/search?text=" + text + "&from=" + from + "&size=" + size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody NewCommentDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items/{}/comment - добавление комментария пользователем {}", itemId, userId);
        return client.post("/items/" + itemId + "/comment", userId, dto);
    }
}
