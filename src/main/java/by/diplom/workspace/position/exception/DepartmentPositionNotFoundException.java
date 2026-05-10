package by.diplom.workspace.position.exception;

import by.diplom.workspace.web.exception.NotFoundException;

public class DepartmentPositionNotFoundException extends NotFoundException {
    public DepartmentPositionNotFoundException(Long id) {
        super("Связка отдел-должность с id=" + id + " не найдена");
    }
}
