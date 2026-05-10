package by.diplom.workspace.position.dto.request.create;

import jakarta.validation.constraints.NotBlank;

public record CreatePositionRequestDto(
        @NotBlank String name
) {}
