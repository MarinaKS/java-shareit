package ru.practicum.shareit.Exceptions;

public class UpdateItemException extends RuntimeException {
    public UpdateItemException() {
    }

    public UpdateItemException(String message) {
        super(message);
    }

    public UpdateItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateItemException(Throwable cause) {
        super(cause);
    }

    public UpdateItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
