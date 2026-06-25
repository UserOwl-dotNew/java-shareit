package ru.practicum.shareit.exception;

public class ErrorIsNotBooker extends RuntimeException {
    public ErrorIsNotBooker(String message) {
        super(message);
    }
}
