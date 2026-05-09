package by.diplom.workspace.web;

import by.diplom.workspace.web.exception.NotFoundException;
import by.diplom.workspace.worker.exception.NicknameAlreadyExistsException;
import by.diplom.workspace.worker.exception.password.InvalidPasswordException;
import by.diplom.workspace.worker.exception.password.PasswordMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleUserNotFound(NotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NicknameAlreadyExistsException.class)
    public ProblemDetail handleNicknameConflict(NicknameAlreadyExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Ошибки валидации (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Ошибка валидации");
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ProblemDetail handleInvalidPassword(InvalidPasswordException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ProblemDetail handlePasswordMismatch(PasswordMismatchException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
