package by.diplom.workspace.worker.dto.profile.response;

import java.util.UUID;

public record UserNicknameResponseDto(
        UUID id,
        String nickname
) {
}
