package by.diplom.workspace.worker.exception.position;

public class DepartmentPositionAlreadyExistsException extends RuntimeException {
    public DepartmentPositionAlreadyExistsException() {
        super("Такая связка отдел-должность уже существует");
    }
}
