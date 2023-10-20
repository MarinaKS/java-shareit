package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotOwnerException extends RuntimeException {
    public UserNotOwnerException() {
    }

    public UserNotOwnerException(String message) {
        super(message);
    }

    public UserNotOwnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotOwnerException(Throwable cause) {
        super(cause);
    }

    public UserNotOwnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
