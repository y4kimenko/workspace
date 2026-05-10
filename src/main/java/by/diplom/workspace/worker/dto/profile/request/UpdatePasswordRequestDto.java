package by.diplom.workspace.worker.dto.profile.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequestDto(
        @NotBlank(message = "Текущий пароль не может быть пустым")
        String currentPassword,

        @NotBlank(message = "Новый пароль не может быть пустым")
        @Size(
                min = 8,
                max = 128,
                message = "Пароль должен содержать от 8 до 128 символов"
        )
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).+$",
                message = "Пароль должен содержать хотя бы одну заглавную букву, одну цифру и один специальный символ"
        )
        String newPassword,


        @NotBlank(message = "Подтверждение пароля не может быть пустым")
        String confirmPassword
) {
}
