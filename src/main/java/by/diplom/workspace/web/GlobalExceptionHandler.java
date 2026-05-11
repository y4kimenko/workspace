package by.diplom.workspace.web;

import by.diplom.workspace.email.exception.CannotDeleteLastEmailException;
import by.diplom.workspace.email.exception.CannotDeletePrimaryEmailException;
import by.diplom.workspace.email.exception.EmailAlreadyExistsException;
import by.diplom.workspace.email.exception.InvalidVerificationCodeException;
import by.diplom.workspace.worker.exception.password.PasswordMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasswordMismatchException.class)
    public ProblemDetail handlePasswordMismatch(PasswordMismatchException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ProblemDetail InvalidVerificationCode(InvalidVerificationCodeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CannotDeletePrimaryEmailException.class)
    public ProblemDetail handleCannotDeletePrimary(CannotDeletePrimaryEmailException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CannotDeleteLastEmailException.class)
    public ProblemDetail handleCannotDeleteLast(CannotDeleteLastEmailException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
