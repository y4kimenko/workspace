package by.diplom.workspace.worker.exception.position;

import by.diplom.workspace.web.exception.NotFoundException;

public class DepartmentNotFoundException extends NotFoundException {
    public DepartmentNotFoundException(Long id) {
        super("Отдел с id=" + id + " не найден");
    }
}
