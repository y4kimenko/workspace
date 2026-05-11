package by.diplom.workspace.worker.email.exception;

import by.diplom.workspace.web.exception.NotFoundException;

public class EmailNotFoundException extends NotFoundException {
    public EmailNotFoundException(String email) {
        super("Email '" + email + "' не найден");
    }
}
