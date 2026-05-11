package by.diplom.workspace.email.exception;

import java.util.UUID;

public class PrimaryEmailNotFoundException extends RuntimeException {
    public PrimaryEmailNotFoundException(UUID userId) {
        super("У пользователя " + userId + " отсутствует основная почта");
    }
}
