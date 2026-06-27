package ru.practicum.shareit.exceptions;

public class ErrorIsOwnerOfRequest extends RuntimeException {
    public ErrorIsOwnerOfRequest(String message) {
        super(message);
    }
}
