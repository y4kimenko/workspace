package by.diplom.workspace.email.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Неверный или истёкший код подтверждения");
    }
}