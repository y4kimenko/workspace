package by.diplom.workspace.favorite.exception;

import by.diplom.workspace.web.ApiError;
import by.diplom.workspace.web.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class FavoriteExceptionHandler {
    @ExceptionHandler(FavoritePlaceNotFoundException.class)
    public ResponseEntity<ApiError> handleFavoritePlaceNotFound(FavoritePlaceNotFoundException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.NOT_FOUND,
                "FAVORITE_PLACE_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(FavoritePlaceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleFavoritePlaceAlreadyExists(FavoritePlaceAlreadyExistsException exception) {
        return GlobalExceptionHandler.buildResponse(
                HttpStatus.BAD_REQUEST,
                "FAVORITE_PLACE_ALREADY_EXISTS",
                exception.getMessage()
        );
    }
}
