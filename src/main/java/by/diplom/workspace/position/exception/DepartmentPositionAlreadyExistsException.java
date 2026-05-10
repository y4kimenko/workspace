package by.diplom.workspace.position.exception;

public class DepartmentPositionAlreadyExistsException extends RuntimeException {
    public DepartmentPositionAlreadyExistsException() {
        super("Такая связка отдел-должность уже существует");
    }
}
