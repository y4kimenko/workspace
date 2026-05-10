package by.diplom.workspace.admin.service;

import by.diplom.workspace.admin.dto.request.CreateUserRequest;
import by.diplom.workspace.admin.dto.response.CreateUserResponse;

public interface AdminUserService {
    CreateUserResponse createUser(CreateUserRequest request);
}
