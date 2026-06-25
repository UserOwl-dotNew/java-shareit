package ru.practicum.shareit.exceptions;

public class ErrorIsNotOwnerAndBooker extends RuntimeException {
    public ErrorIsNotOwnerAndBooker(String message) {
        super(message);
    }
}
