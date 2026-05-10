package by.diplom.workspace.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddEmailRequestDto(
        @NotBlank @Email(message = "Некорректный формат email")
        String email
) {
}