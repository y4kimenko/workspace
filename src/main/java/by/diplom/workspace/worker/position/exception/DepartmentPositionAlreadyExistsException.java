package by.diplom.workspace.worker.position.exception;

public class DepartmentPositionAlreadyExistsException extends RuntimeException {
    public DepartmentPositionAlreadyExistsException() {
        super("Такая связка отдел-должность уже существует");
    }
}
