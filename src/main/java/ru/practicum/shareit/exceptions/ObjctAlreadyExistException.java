package ru.practicum.shareit.exceptions;

public class ObjctAlreadyExistException extends RuntimeException {
    public ObjctAlreadyExistException() {
    }

    public ObjctAlreadyExistException(String message) {
        super(message);
    }

    public ObjctAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjctAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public ObjctAlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
