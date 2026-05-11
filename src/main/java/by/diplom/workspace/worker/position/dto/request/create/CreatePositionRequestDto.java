package by.diplom.workspace.worker.position.dto.request.create;

import jakarta.validation.constraints.NotBlank;

public record CreatePositionRequestDto(
        @NotBlank String name
) {
}
