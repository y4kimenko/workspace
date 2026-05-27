package by.diplom.workspace.admin.users.dto.response;

import by.diplom.workspace.admin.users.dto.EnumsDto.UserTypeRequest;
import by.diplom.workspace.worker.position.dto.response.DepartmentPositionResponseDto;

import java.util.UUID;

public record UserResponseDto(
    UUID id,
    String fullName,
    String nickName,
    DepartmentPositionResponseDto departamentPosition,
    UserTypeRequest userType
) {}
