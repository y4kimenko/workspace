package by.diplom.workspace.worker.worker.exception.password;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("Новый пароль и подтверждение не совпадают");
    }
}