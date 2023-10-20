package ru.practicum.shareit.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException() {
    }

    public ItemUnavailableException(String message) {
        super(message);
    }

    public ItemUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemUnavailableException(Throwable cause) {
        super(cause);
    }

    public ItemUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
