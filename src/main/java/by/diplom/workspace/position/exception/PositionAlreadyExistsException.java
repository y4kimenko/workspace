package by.diplom.workspace.position.exception;

public class PositionAlreadyExistsException extends RuntimeException {
    public PositionAlreadyExistsException(String name) {
        super("Должность с названием \"" + name + "\" уже существует");
    }
}