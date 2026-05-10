package by.diplom.workspace.worker.dto.profile.response;

import java.util.List;
import java.util.UUID;

public record UserPublicProfileResponseDto(

        String fullName,
        String nickname,
        String avatarPath,
        String bio,
        String pronoun,

        DepartmentPositionInfo departmentPosition,
        String publicEmail,

        List<SocialLinkInfo> socialLinks
) {
    public record DepartmentPositionInfo(
            String positionName,
            String departmentName
    ) {}

    public record SocialLinkInfo(
            UUID id,
            String platformCode,   // "GITHUB", "VK" и т.д.
            String platformName,   // "GitHub", "ВКонтакте" и т.д.
            String url
    ) {}
}