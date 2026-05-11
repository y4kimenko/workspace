package by.diplom.workspace.worker.dto.profile.response;

public record UserPartPublicProfileResponseDto(
        String bio,
        PronounResponseDto pronoun,
        String publicEmail
) {
}
