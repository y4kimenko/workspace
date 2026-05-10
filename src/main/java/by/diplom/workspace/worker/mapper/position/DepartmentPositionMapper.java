package by.diplom.workspace.worker.mapper.position;

import by.diplom.workspace.worker.dto.position.response.DepartmentPositionResponseDto;
import by.diplom.workspace.worker.dto.position.response.DepartmentResponseDto;
import by.diplom.workspace.worker.dto.position.response.PositionResponseDto;
import by.diplom.workspace.worker.model.user.profile.position.DepartmentPosition;

public class DepartmentPositionMapper {
    public static DepartmentPositionResponseDto toResponseDto(DepartmentPosition dp) {
        DepartmentResponseDto departmentResponse = dp.getDepartment() != null
                ? new DepartmentResponseDto(
                dp.getDepartment().getId(),
                dp.getDepartment().getName())
                : null;

        PositionResponseDto positionResponse = dp.getPosition() != null
                ? new PositionResponseDto(
                dp.getPosition().getId(),
                dp.getPosition().getName())
                : null;

        return new DepartmentPositionResponseDto(dp.getId(), departmentResponse, positionResponse);
    }
}
