package by.diplom.workspace.worker.position.mapper;

import by.diplom.workspace.worker.position.dto.response.DepartmentResponseDto;
import by.diplom.workspace.worker.position.model.Department;

public class DepartmentMapper {
    public static DepartmentResponseDto toResponseDto(Department d) {
        return new DepartmentResponseDto(d.getId(), d.getName());
    }
}
