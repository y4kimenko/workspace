package by.diplom.workspace.worker.position.exception;

public class PositionAlreadyExistsException extends RuntimeException {
    public PositionAlreadyExistsException(String name) {
        super("Должность с названием \"" + name + "\" уже существует");
    }
}