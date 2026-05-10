package by.diplom.workspace.worker.dto.profile.response;

import java.util.UUID;

public record UserBioResponseDto(
        UUID id,
        String bio
) {
}
