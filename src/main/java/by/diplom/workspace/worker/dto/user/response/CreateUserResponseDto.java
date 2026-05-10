package by.diplom.workspace.worker.dto.user.response;

import java.util.UUID;

public record CreateUserResponseDto(
        UUID id,
        String nickname
) { }
