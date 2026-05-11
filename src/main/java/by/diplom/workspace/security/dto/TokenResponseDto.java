package by.diplom.workspace.security.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
