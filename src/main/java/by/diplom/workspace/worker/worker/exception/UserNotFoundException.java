package by.diplom.workspace.worker.worker.exception;

import by.diplom.workspace.web.exception.NotFoundException;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(UUID id) {
        super("Пользователь с id " + id + " не найден");
    }
}