package by.diplom.workspace.admin.request_registration.exception;

public class RegistrationRequestAlreadyCreatedException extends RuntimeException {
    public RegistrationRequestAlreadyCreatedException(long registrationRequestId) {
        super("Заявка на создание пользователя с id %s уже выполнена.".formatted(registrationRequestId));
    }
}
