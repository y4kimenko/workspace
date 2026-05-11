package by.diplom.workspace.worker.position.exception;

public class DepartmentAlreadyExistsException extends RuntimeException {
    public DepartmentAlreadyExistsException(String name) {
        super("Отдел с названием \"" + name + "\" уже существует");
    }
}
