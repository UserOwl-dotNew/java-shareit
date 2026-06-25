package ru.practicum.shareit.exception;

public class ErrorIsNotOwnerAndBooker extends RuntimeException {
    public ErrorIsNotOwnerAndBooker(String message) {
        super(message);
    }
}
