package by.diplom.workspace.worker.exception.position;

public class DepartmentAlreadyExistsException extends RuntimeException {
    public DepartmentAlreadyExistsException(String name) {
        super("Отдел с названием \"" + name + "\" уже существует");
    }
}
