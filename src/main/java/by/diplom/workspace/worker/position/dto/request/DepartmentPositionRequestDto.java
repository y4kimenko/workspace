package by.diplom.workspace.worker.position.dto.request;

import jakarta.validation.constraints.NotNull;

public record DepartmentPositionRequestDto(
        @NotNull(message = "ID отдела обязателен")
        Long departmentId,

        @NotNull(message = "ID должности обязателен")
        Long positionId
) {
}
