package by.diplom.workspace.worker.worker.service;

import by.diplom.workspace.worker.worker.dto.user.request.CreateUserRequestDtoOld;
import by.diplom.workspace.worker.worker.dto.user.response.CreateUserResponseDto;


public interface AdminUserService {
    CreateUserResponseDto createUser(CreateUserRequestDtoOld request);
}
