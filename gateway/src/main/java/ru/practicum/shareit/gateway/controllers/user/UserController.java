package ru.practicum.shareit.gateway.controllers.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.user.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final BaseClient client;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserDto dto) {
        log.info("POST /users - запрос на добавление пользователя");
        return client.post("/users", dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable Long userId, @RequestBody UserDto dto) {
        log.info("PATCH /users/{} - запрос на обновление пользователя", userId);
        return client.patch("/users/" + userId, null, dto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> get(@PathVariable Long userId) {
        log.info("GET /users/{} - запрос на получение пользователя", userId);
        return client.get("/users/" + userId);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        log.info("GET /users - запрос на получение всех пользователей");
        return client.get("/users");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable Long userId) {
        log.info("DELETE /users/{} - запрос на удаление пользователя", userId);
        return client.delete("/users/" + userId, null);
    }
}
