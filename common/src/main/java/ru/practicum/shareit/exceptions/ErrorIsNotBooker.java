package ru.practicum.shareit.exceptions;

public class ErrorIsNotBooker extends RuntimeException {
    public ErrorIsNotBooker(String message) {
        super(message);
    }
}
