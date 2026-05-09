package by.diplom.workspace.email.dto.response;

import java.util.UUID;

public record UserEmailResponse(
        UUID id,
        String email,
        boolean verified,
        boolean primary,
        boolean publicEmail
) {
}
