package ru.practicum.shareit.item.validators;

import jakarta.validation.Valid;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemValidator {
    public static void itemValidator(@Valid ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Название вещи должно быть указано");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи должно быть указано");
        }

        if (item.getAvailable() == null) {
            throw new ValidationException("Статус бронирования должен быть указан");
        }
    }
}
