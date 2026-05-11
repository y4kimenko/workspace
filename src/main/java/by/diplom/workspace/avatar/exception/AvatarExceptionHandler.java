package by.diplom.workspace.avatar.exception;

import by.diplom.workspace.web.ApiError;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;


@RestControllerAdvice
public class AvatarExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "ENTITY_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(AvatarException.class)
    public ResponseEntity<ApiError> handleAvatarException(AvatarException exception) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "AVATAR_ERROR",
                exception.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException exception) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                exception.getMessage()
        );
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity
                .status(status)
                .body(new ApiError(
                        status.value(),
                        code,
                        message,
                        Instant.now()
                ));
    }
}
