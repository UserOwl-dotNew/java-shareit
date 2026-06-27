package ru.practicum.shareit.exceptions;

public class OwnerCannotBookHisItemException extends RuntimeException {
    public OwnerCannotBookHisItemException(String message) {
        super(message);
    }
}
