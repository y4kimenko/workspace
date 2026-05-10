package by.diplom.workspace.worker.exception.position;

import by.diplom.workspace.web.exception.NotFoundException;

public class PositionNotFoundException extends NotFoundException {
    public PositionNotFoundException(Long id) {
        super("Должность с id=" + id + " не найдена");
    }
}