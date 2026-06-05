package ru.practicum.shareit.exception;

public class OwnerCannotBookHisItemException extends RuntimeException {
    public OwnerCannotBookHisItemException(String message) {
        super(message);
    }
}
