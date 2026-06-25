package ru.practicum.shareit.exceptions;

public class NotAvailableItemException extends RuntimeException {
    public NotAvailableItemException(String message) {
        super(message);
    }
}
