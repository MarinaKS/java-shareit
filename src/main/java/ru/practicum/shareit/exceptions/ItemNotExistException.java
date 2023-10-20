package ru.practicum.shareit.exceptions;

public class ItemNotExistException extends RuntimeException {
    public ItemNotExistException() {
    }

    public ItemNotExistException(String message) {
        super(message);
    }
}
