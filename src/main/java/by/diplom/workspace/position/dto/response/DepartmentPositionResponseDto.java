package by.diplom.workspace.position.dto.response;

public record DepartmentPositionResponseDto(
        Long id,
        DepartmentResponseDto department,
        PositionResponseDto position
) {}
