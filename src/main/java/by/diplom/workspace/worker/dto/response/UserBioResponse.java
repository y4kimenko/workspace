package by.diplom.workspace.worker.dto.response;

import java.util.UUID;

public record UserBioResponse(
        UUID id,
        String bio
) {
}
