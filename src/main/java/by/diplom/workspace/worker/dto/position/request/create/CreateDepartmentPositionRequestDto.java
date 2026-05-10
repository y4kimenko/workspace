package by.diplom.workspace.worker.dto.position.request.create;

import jakarta.validation.constraints.NotNull;

public record CreateDepartmentPositionRequestDto(
        @NotNull
        Long departmentId,
        @NotNull
        Long positionId
) {
}
