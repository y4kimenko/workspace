package by.diplom.workspace.worker.email.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Неверный или истёкший код подтверждения");
    }
}