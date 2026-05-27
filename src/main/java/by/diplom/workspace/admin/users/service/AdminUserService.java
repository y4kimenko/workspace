package by.diplom.workspace.admin.users.service;

import by.diplom.workspace.admin.users.dto.request.CreateUserRequestDto;
import by.diplom.workspace.admin.users.dto.request.UserUpdateRequestDto;
import by.diplom.workspace.admin.users.dto.response.CreateUserResponseDto;
import by.diplom.workspace.admin.users.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;


public interface AdminUserService {
    CreateUserResponseDto createUser(CreateUserRequestDto request);
    void deleteUser(UUID id);

    UserResponseDto update(UUID id, UserUpdateRequestDto request);

    List<UserResponseDto> getAllUser();
}
