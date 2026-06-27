package ru.practicum.shareit.exceptions;

public class ErrorIsNotOwner extends RuntimeException {
    public ErrorIsNotOwner(String message) {
        super(message);
    }
}
