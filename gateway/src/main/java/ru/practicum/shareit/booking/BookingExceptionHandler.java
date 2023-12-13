package ru.practicum.shareit.booking;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Map;

@RestControllerAdvice
public class BookingExceptionHandler {
    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConversionFailedException ex) {
        Boolean b = ex.getTargetType().isAssignableTo(TypeDescriptor.valueOf(BookingState.class));
        String error = "BAD_REQUEST";
        if (b) {
            error = "Unknown state: " + ex.getValue();
        }
        return new ResponseEntity<>(Map.of(
                "error", error
        ), HttpStatus.BAD_REQUEST);
    }
}
