package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentValidationException extends RuntimeException {
    public CommentValidationException() {
    }

    public CommentValidationException(String message) {
        super(message);
    }

    public CommentValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommentValidationException(Throwable cause) {
        super(cause);
    }

    public CommentValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
