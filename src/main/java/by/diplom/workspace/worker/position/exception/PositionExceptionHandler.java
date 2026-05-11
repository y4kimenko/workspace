package by.diplom.workspace.worker.position.exception;

import by.diplom.workspace.web.ApiError;
import by.diplom.workspace.web.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PositionExceptionHandler {
    @ExceptionHandler(DepartmentAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDepartmentAlreadyExists(DepartmentAlreadyExistsException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.CONFLICT,
                "DEPARTMENT_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<ApiError> handleDepartmentNotFound(DepartmentNotFoundException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.NOT_FOUND,
                "DEPARTMENT_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DepartmentPositionAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDepartmentPositionAlreadyExists(DepartmentPositionAlreadyExistsException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.CONFLICT,
                "DEPARTMENT_POSITION_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(DepartmentPositionNotFoundException.class)
    public ResponseEntity<ApiError> handleDepartmentPositionNotFound(DepartmentPositionNotFoundException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.NOT_FOUND,
                "DEPARTMENT_POSITION_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(PositionAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlePositionAlreadyExists(PositionAlreadyExistsException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.CONFLICT,
                "POSITION_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(PositionNotFoundException.class)
    public ResponseEntity<ApiError> handlePositionNotFound(PositionNotFoundException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.NOT_FOUND,
                "POSITION_NOT_FOUND",
                exception.getMessage()
        );
    }
}
