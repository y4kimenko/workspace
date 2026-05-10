package by.diplom.workspace.position.mapper;

import by.diplom.workspace.position.dto.response.DepartmentResponseDto;
import by.diplom.workspace.position.model.Department;

public class DepartmentMapper {
    public static DepartmentResponseDto toResponseDto(Department d) {
        return new DepartmentResponseDto(d.getId(), d.getName());
    }
}
