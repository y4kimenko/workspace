package by.diplom.workspace.worker.email.exception;

public class CannotDeletePrimaryEmailException extends RuntimeException {
    public CannotDeletePrimaryEmailException() {
        super("Нельзя удалить основной email. Сначала смените основной адрес.");
    }
}