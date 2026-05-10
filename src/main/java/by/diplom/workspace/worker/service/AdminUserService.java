package by.diplom.workspace.worker.service;

import by.diplom.workspace.worker.dto.user.request.CreateUserRequestDto;
import by.diplom.workspace.worker.dto.user.response.CreateUserResponseDto;

public interface AdminUserService {
    CreateUserResponseDto createUser(CreateUserRequestDto request);
}
