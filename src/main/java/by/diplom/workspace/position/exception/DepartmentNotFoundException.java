package by.diplom.workspace.position.exception;

import by.diplom.workspace.web.exception.NotFoundException;

public class DepartmentNotFoundException extends NotFoundException {
    public DepartmentNotFoundException(Long id) {
        super("Отдел с id=" + id + " не найден");
    }
}
