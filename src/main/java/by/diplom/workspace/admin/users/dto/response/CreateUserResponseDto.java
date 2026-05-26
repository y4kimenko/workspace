package by.diplom.workspace.admin.users.dto.response;

import java.util.UUID;

public record CreateUserResponseDto(
        UUID id,
        String nickname
) {
}
