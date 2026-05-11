package by.diplom.workspace.worker.position.dto.request.create;

import jakarta.validation.constraints.NotNull;

public record CreateDepartmentPositionRequestDto(
        @NotNull
        Long departmentId,
        @NotNull
        Long positionId
) {
}
