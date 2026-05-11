package by.diplom.workspace.place.exception;

import by.diplom.workspace.web.ApiError;
import by.diplom.workspace.web.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PlaceExceptionHandler {

    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ApiError> handlePlaceNotFound(PlaceNotFoundException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.NOT_FOUND,
                "PLACE_NOT_FOUND",
                exception.getMessage()
        );
    }
}
