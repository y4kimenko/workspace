package by.diplom.workspace.position.exception;

public class DepartmentAlreadyExistsException extends RuntimeException {
    public DepartmentAlreadyExistsException(String name) {
        super("Отдел с названием \"" + name + "\" уже существует");
    }
}
