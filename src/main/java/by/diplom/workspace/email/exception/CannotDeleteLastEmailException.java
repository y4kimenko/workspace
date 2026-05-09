package by.diplom.workspace.email.exception;

public class CannotDeleteLastEmailException extends RuntimeException {
    public CannotDeleteLastEmailException() {
        super("Нельзя удалить единственный email аккаунта.");
    }
}