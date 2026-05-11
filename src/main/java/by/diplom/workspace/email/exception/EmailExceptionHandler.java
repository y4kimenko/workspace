package by.diplom.workspace.email.exception;

import by.diplom.workspace.web.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class EmailExceptionHandler {
    @ExceptionHandler(CannotDeleteLastEmailException.class)
    public ResponseEntity<ApiError> handleCannotDeleteLastEmail(CannotDeleteLastEmailException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "CANNOT_DELETE_LAST_EMAIL",
                exception.getMessage()
        );
    }

    @ExceptionHandler(CannotDeletePrimaryEmailException.class)
    public ResponseEntity<ApiError> handleCannotDeletePrimaryEmail(CannotDeletePrimaryEmailException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "CANNOT_DELETE_PRIMARY_EMAIL",
                exception.getMessage()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "EMAIL_ALREADY_EXISTS",
                exception.getMessage()
        );
    }

    @ExceptionHandler(EmailLimitExceededException.class)
    public ResponseEntity<ApiError> handleEmailLimitExceeded(EmailLimitExceededException exception) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "EMAIL_LIMIT_EXCEEDED",
                exception.getMessage()
        );
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ApiError> handleEmailNotFound(EmailNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "EMAIL_NOT_FOUND",
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ApiError> handleInvalidVerificationCode(InvalidVerificationCodeException exception) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_VERIFICATION_CODE",
                exception.getMessage()
        );
    }

    @ExceptionHandler(PrimaryEmailNotFoundException.class)
    public ResponseEntity<ApiError> handlePrimaryEmailNotFound(PrimaryEmailNotFoundException exception) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "PRIMARY_EMAIL_NOT_FOUND",
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
