package by.diplom.workspace.admin.users.service;

import by.diplom.workspace.admin.users.dto.request.CreateUserRequestDto;
import by.diplom.workspace.admin.users.dto.response.CreateUserResponseDto;


public interface AdminUserService {
    CreateUserResponseDto createUser(CreateUserRequestDto request);
}
