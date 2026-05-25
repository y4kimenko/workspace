package by.diplom.workspace.admin.request_registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RegistrationRequestAlreadyExistsException extends RuntimeException {

    public RegistrationRequestAlreadyExistsException(String email) {
        super("Активная заявка на регистрацию с email " + email + " уже существует");
    }
}
