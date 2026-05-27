package by.diplom.workspace.worker.worker.mapper;

import by.diplom.workspace.admin.users.dto.EnumsDto.UserTypeRequest;
import by.diplom.workspace.admin.users.dto.response.UserResponseDto;
import by.diplom.workspace.worker.position.mapper.DepartmentPositionMapper;
import by.diplom.workspace.worker.worker.model.Employee;
import by.diplom.workspace.worker.worker.model.GroupManager;
import by.diplom.workspace.worker.worker.model.user.User;

public class UserMapper {
    public static UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getNickname(),
                DepartmentPositionMapper.toResponseDto(user.getDepartmentPosition()),
                getUserType(user)
        );
    }

    private static UserTypeRequest getUserType(User user) {
        if (user instanceof Employee) {
            return UserTypeRequest.EMPLOYEE;
        }

        if (user instanceof GroupManager) {
            return UserTypeRequest.GROUP_MANAGER;
        }

        throw new IllegalArgumentException("Неизвестный тип пользователя: " + user.getClass().getName());
    }
}
