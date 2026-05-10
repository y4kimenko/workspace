package by.diplom.workspace.admin.dto.response;

import java.util.UUID;

public record CreateUserResponse(
        UUID id,
        String nickname
) { }
