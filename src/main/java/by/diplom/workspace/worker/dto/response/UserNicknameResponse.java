package by.diplom.workspace.worker.dto.response;

import java.util.UUID;

public record UserNicknameResponse(
        UUID id,
        String nickname
) {
}
