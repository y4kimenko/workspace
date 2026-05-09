package by.diplom.workspace.email.dto.request;

import jakarta.validation.constraints.Email;

public record UpdatePublicEmailRequest(
        @Email(message = "Некорректный формат email")
        String email
) {
}
