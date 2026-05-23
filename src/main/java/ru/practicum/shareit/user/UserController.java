package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validators.UserValidator;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getAll() {
        log.info("Get /users - запрос на получение всех пользователей");
        Collection<UserDto> users = userService.getAllUsers();
        log.info("Get /users - выполнен usersSize={}", users.size());
        return users;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getById(@PathVariable("id") Long id) {
        log.info("Get /users/{id} - запрос на получение пользователя по id, id={}", id);
        UserDto dto = userService.findById(id);
        log.info("Get /users/{id} - запрос на получение пользователя выполнен, dto={}", dto);
        return dto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        log.info("Post /users - запрос на добавление пользователя");
        UserValidator.UserDtoValidator(userDto);
        UserDto dto = userService.saveUser(userDto);
        log.info("Post /users - пользователь успешно добавлен");
        return dto;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        log.info("Patch /users/{id} - запрос на обновления пользователя");
        UserDto dto = userService.updateUser(id, userDto);
        log.info("Patch /users/{id} - пользователь успешно обновлен userDto={}", userDto);
        return dto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        log.info("Delete /users/{id} - запрос на удаление пользователя");
        userService.deleteUser(id);
        log.info("Delete /users/{id} - пользователь успешно удален");
    }
}
