package by.diplom.workspace.worker.dto.position.request.update;

import jakarta.validation.constraints.NotBlank;

public record UpdatePositionRequestDto(
        @NotBlank(message = "Название должности не может быть пустым")
        String name
) {}
