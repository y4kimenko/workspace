package by.diplom.workspace.email.exception;

public class CannotDeletePrimaryEmailException extends RuntimeException {
    public CannotDeletePrimaryEmailException() {
        super("Нельзя удалить основной email. Сначала смените основной адрес.");
    }
}