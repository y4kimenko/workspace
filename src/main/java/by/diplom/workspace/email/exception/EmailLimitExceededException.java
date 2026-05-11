package by.diplom.workspace.email.exception;

public class EmailLimitExceededException extends RuntimeException {
    public EmailLimitExceededException(int limit) {
        super("Нельзя добавить больше " + limit + " email-адресов");
    }
}
