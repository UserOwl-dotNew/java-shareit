package ru.practicum.shareit.exception;

public class ErrorIsNotOwner extends RuntimeException {
    public ErrorIsNotOwner(String message) {
        super(message);
    }
}
