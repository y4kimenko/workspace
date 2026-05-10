package by.diplom.workspace.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequestDto(
        @NotBlank(message = "Email не может быть пустым")
        @Email
        String email,

        @NotBlank(message = "Код не может быть пустым")
        @Size(min = 6, max = 6, message = "Код должен состоять из 6 символов")
        String code
) {
}