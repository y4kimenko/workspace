package by.diplom.workspace.admin.request_registration.exception;

public class RegistrationRequestAlreadyCancelledException extends RuntimeException {

    public RegistrationRequestAlreadyCancelledException(Long registrationRequestId) {
        super("Заявка на создание пользователя с id %s уже отменена.".formatted(registrationRequestId));
    }
}
