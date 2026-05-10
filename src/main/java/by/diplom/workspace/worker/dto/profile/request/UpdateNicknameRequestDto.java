package by.diplom.workspace.worker.dto.profile.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNicknameRequestDto(
        @NotBlank(message = "Никнейм не может быть пустым")
        @Size(min = 3, max = 32, message = "Никнейм должен быть от 3 до 32 символов")
        String nickname
) {
}