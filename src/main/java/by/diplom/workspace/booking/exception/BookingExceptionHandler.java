package by.diplom.workspace.booking.exception;

import by.diplom.workspace.web.ApiError;
import by.diplom.workspace.web.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BookingExceptionHandler {
    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ApiError> handleConflict(BookingConflictException ex) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.CONFLICT,
                "BOOKING_CONFLICT",
                ex.getMessage()
        );
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(BookingNotFoundException ex) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.NOT_FOUND,
                "ENTITY_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " +
                        Objects.requireNonNullElse(
                                fieldError.getDefaultMessage(),
                                "Ошибка валидации"
                        )
                )
                .distinct()
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Ошибка валидации";
        }

        return GlobalExceptionHandler.buildResponse(
                HttpStatus.BAD_REQUEST,
                "METHOD_ARGUMENT_NOT_VALID",
                message
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "ILLEGAL_STATE",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "Недостаточно прав для выполнения данного действия"
        );
    }
}
