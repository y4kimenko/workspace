package by.diplom.workspace.worker.dto.profile.request;

import jakarta.validation.constraints.Max;

public record UpdateBioRequestDto(
        @Max(value = 300, message = "Био не может быть больше 300 символов")
        String bio
) {
}
