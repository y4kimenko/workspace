package by.diplom.workspace.worker.email.exception;

public class CannotDeleteLastEmailException extends RuntimeException {
    public CannotDeleteLastEmailException() {
        super("Нельзя удалить единственный email аккаунта.");
    }
}