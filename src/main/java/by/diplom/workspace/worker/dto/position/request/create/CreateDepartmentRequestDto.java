package by.diplom.workspace.worker.dto.position.request.create;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentRequestDto(
        @NotBlank String name
) {}
