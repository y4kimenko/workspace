package by.diplom.workspace.worker.dto.position.response;

public record DepartmentPositionResponseDto(
        Long id,
        DepartmentResponseDto department,
        PositionResponseDto position
) {}
