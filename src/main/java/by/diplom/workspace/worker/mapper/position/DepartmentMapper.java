package by.diplom.workspace.worker.mapper.position;

import by.diplom.workspace.worker.dto.position.response.DepartmentResponseDto;
import by.diplom.workspace.worker.model.user.profile.position.Department;

public class DepartmentMapper {
    public static DepartmentResponseDto toResponseDto(Department d) {
        return new DepartmentResponseDto(d.getId(), d.getName());
    }
}
