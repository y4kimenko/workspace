package by.diplom.workspace.worker.email.dto.response;

import java.util.UUID;

public record UserEmailResponseDto(
        UUID id,
        String email,
        boolean verified,
        boolean primary,
        boolean publicEmail
) {
}
