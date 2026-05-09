package by.diplom.workspace.worker.exception;

public class NicknameAlreadyExistsException extends RuntimeException {
    public NicknameAlreadyExistsException(String nickname) {
        super("Никнейм '" + nickname + "' уже занят");
    }
}
