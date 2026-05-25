package by.diplom.workspace.admin.request_registration.exception;

public class RegistrationRequestEmailNotVerifiedException extends RuntimeException {
    public RegistrationRequestEmailNotVerifiedException(String email) {
        super("Невозможно создать пользователя: почта в заявке  %s ещё не подтверждена."
                .formatted(email));
    }
}
