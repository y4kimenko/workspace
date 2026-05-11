package by.diplom.workspace.worker.position.dto.response;

public record DepartmentPositionResponseDto(
        Long id,
        DepartmentResponseDto department,
        PositionResponseDto position
) {
}
