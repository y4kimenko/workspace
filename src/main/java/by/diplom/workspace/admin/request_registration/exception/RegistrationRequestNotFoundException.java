package by.diplom.workspace.admin.request_registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RegistrationRequestNotFoundException extends RuntimeException {

    public RegistrationRequestNotFoundException(String email) {
        super("Активная заявка на регистрацию с email " + email + " не найдена");
    }
}
