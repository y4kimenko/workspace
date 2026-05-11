package by.diplom.workspace.position.exception;

import by.diplom.workspace.web.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class PositionExceptionHandler {
    @ExceptionHandler(DepartmentAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDepartmentAlreadyExists(DepartmentAlreadyExistsException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "DEPARTMENT_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiError> handleDepartmentNotFound(DepartmentNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "DEPARTMENT_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DepartmentPositionAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDepartmentPositionAlreadyExists(DepartmentPositionAlreadyExistsException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "DEPARTMENT_POSITION_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DepartmentPositionNotFoundException.class)
    public ResponseEntity<ApiError> handleDepartmentPositionNotFound(DepartmentPositionNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "DEPARTMENT_POSITION_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(PositionAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlePositionAlreadyExists(PositionAlreadyExistsException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "POSITION_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(PositionNotFoundException.class)
    public ResponseEntity<ApiError> handlePositionNotFound(PositionNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "POSITION_NOT_FOUND",
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
