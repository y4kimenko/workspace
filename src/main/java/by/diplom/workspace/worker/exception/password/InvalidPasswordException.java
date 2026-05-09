package by.diplom.workspace.worker.exception.password;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Неверный текущий пароль");
    }
}