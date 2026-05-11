package by.diplom.workspace.worker.email.exception;

public class EmailLimitExceededException extends RuntimeException {
    public EmailLimitExceededException(int limit) {
        super("Нельзя добавить больше " + limit + " email-адресов");
    }
}
