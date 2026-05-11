package by.diplom.workspace.position.dto.request.update;

import jakarta.validation.constraints.NotBlank;

public record UpdatePositionRequestDto(
        @NotBlank(message = "Название должности не может быть пустым")
        String name
) {
}
