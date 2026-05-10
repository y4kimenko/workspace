package by.diplom.workspace.email.dto.response;

import java.util.UUID;

public record UserEmailResponseDto(
        UUID id,
        String email,
        boolean verified,
        boolean primary,
        boolean publicEmail
) {
}
