package ru.practicum.shareit.Exceptions;

public class ItemNotExistException extends RuntimeException {
    public ItemNotExistException() {
    }

    public ItemNotExistException(String message) {
        super(message);
    }
}
