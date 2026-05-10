package by.diplom.workspace.worker.exception.position;

public class PositionAlreadyExistsException extends RuntimeException {
    public PositionAlreadyExistsException(String name) {
        super("Должность с названием \"" + name + "\" уже существует");
    }
}