package ru.practicum.shareit.user.validators;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.exceptions.ValidationException;

@Slf4j
public class UserValidator {
    public static void userDtoValidator(UserDto user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Email должен быть указан");
            throw new ValidationException("Email должен быть указан");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Email должен содержать символ @");
            throw new ValidationException("Email должен содержать символ @");
        }
    }
}
